package com.tienda.gestion.application.port.in;

import com.tienda.gestion.domain.model.Producto;

import java.util.List;

public interface ProductoUseCase {

    Producto crearProducto(Producto producto);

    List<Producto> listarProductos();

    Producto buscarProductoPorId(Long id);

    Producto actualizarProducto(Long id, Producto producto);

    void eliminarProducto(Long id);
    
    List<Producto> listarProductosStockBajo();
    
    List<Producto> listarProductosSinStock();
}
