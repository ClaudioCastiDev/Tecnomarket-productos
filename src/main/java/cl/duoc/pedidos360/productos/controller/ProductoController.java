package cl.duoc.pedidos360.productos.controller;

import cl.duoc.pedidos360.productos.dto.ProductoRequest;
import cl.duoc.pedidos360.productos.dto.ProductoResponse;
import cl.duoc.pedidos360.productos.model.Producto;
import cl.duoc.pedidos360.productos.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * La autorización (scopes y roles) se define en SecurityConfig:
 * GET -> scope productos-read | POST -> scope productos-write | DELETE -> rol ADMIN.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoRepository repository;

	public ProductoController(ProductoRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<ProductoResponse> listar() {
		return repository.findAll().stream().map(ProductoResponse::from).toList();
	}

	@GetMapping("/{id}")
	public ProductoResponse obtener(@PathVariable Long id) {
		return repository.findById(id)
				.map(ProductoResponse::from)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
		Producto producto = new Producto(request.nombre(), request.precio(), request.stock());
		return ProductoResponse.from(repository.save(producto));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		if (!repository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
		}
		repository.deleteById(id);
	}
}
