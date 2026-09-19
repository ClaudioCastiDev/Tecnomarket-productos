package cl.duoc.pedidos360.productos.config;

import cl.duoc.pedidos360.productos.model.Producto;
import cl.duoc.pedidos360.productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Inserta datos de ejemplo si la tabla está vacía, para que la demo muestre resultados. */
@Configuration
public class DataSeeder {

	@Bean
	CommandLineRunner seedProductos(ProductoRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				repository.saveAll(List.of(
						new Producto("Teclado mecánico", 39990, 25),
						new Producto("Monitor 27 pulgadas", 189990, 10),
						new Producto("Mouse inalámbrico", 15990, 40)));
			}
		};
	}
}
