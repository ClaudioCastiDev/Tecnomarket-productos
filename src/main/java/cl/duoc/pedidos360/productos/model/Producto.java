package cl.duoc.pedidos360.productos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	/** Precio en pesos chilenos (CLP), sin decimales. */
	@Column(nullable = false)
	private Integer precio;

	@Column(nullable = false)
	private Integer stock;

	protected Producto() {
		// requerido por JPA
	}

	public Producto(String nombre, Integer precio, Integer stock) {
		this.nombre = nombre;
		this.precio = precio;
		this.stock = stock;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public Integer getPrecio() {
		return precio;
	}

	public Integer getStock() {
		return stock;
	}
}
