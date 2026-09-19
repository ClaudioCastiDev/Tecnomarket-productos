package cl.duoc.pedidos360.productos.dto;

import cl.duoc.pedidos360.productos.model.Producto;

public record ProductoResponse(
		Long id,
		String nombre,
		Integer precio,
		Integer stock) {

	public static ProductoResponse from(Producto p) {
		return new ProductoResponse(p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
	}
}
