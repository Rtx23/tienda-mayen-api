package com.tienda.gestion.application.port.in;

import com.tienda.gestion.domain.model.Producto;

import java.util.List;

// Define las operaciones para gestionar los productos.
public interface ProductoUseCase {

    // Crea un producto nuevo.
    Producto crearProducto(Producto producto);

    // Obtiene todos los productos registrados.
    List<Producto> listarProductos();

    // Busca un producto por su ID.
    Producto buscarProductoPorId(Long id);

    // Actualiza los datos de un producto.
    Producto actualizarProducto(Long id, Producto producto);

    // Elimina un producto por su ID.
    void eliminarProducto(Long id);

    // Obtiene los productos con stock bajo.
    List<Producto> listarProductosStockBajo();

    // Obtiene los productos que no tienen stock.
    List<Producto> listarProductosSinStock();
}
