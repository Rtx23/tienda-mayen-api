package com.tienda.gestion.application.service;

import com.tienda.gestion.application.exception.ProductoNoEncontradoException;
import com.tienda.gestion.application.port.in.ProductoUseCase;
import com.tienda.gestion.application.port.out.ProductoRepository;
import com.tienda.gestion.domain.model.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements ProductoUseCase {

    // Dependencia del repositorio para gestionar los productos.
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto crearProducto(Producto producto) {

        // Valida los datos antes de guardar el producto.
        validarProducto(producto);

        return productoRepository.guardar(producto);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.listarTodos();
    }

    @Override
    public Producto buscarProductoPorId(Long id) {

        // Lanza una excepción si el producto no existe.
        return productoRepository.buscarPorId(id)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(id)
                );
    }

    @Override
    public Producto actualizarProducto(Long id, Producto producto) {

        // Verifica que el producto exista antes de actualizarlo.
        buscarProductoPorId(id);

        // Valida los nuevos datos del producto.
        validarProducto(producto);

        producto.setId(id);

        return productoRepository.actualizar(producto);
    }

    @Override
    public void eliminarProducto(Long id) {

        // Verifica que exista antes de eliminarlo.
        buscarProductoPorId(id);

        productoRepository.eliminar(id);
    }

    // Centraliza las validaciones de los datos del producto.
    private void validarProducto(Producto producto) {

        if (producto.getNombre() == null ||
            producto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio"
            );
        }

        if (producto.getPrecio() <= 0) {

            throw new IllegalArgumentException(
                    "El precio debe ser mayor a cero"
            );
        }

        if (producto.getStock() < 0) {

            throw new IllegalArgumentException(
                    "El stock no puede ser negativo"
            );
        }

        if (producto.getFechaVencimiento() == null) {

            throw new IllegalArgumentException(
                    "La fecha de vencimiento es obligatoria"
            );
        }

        if (producto.getStockMinimo() < 0) {

            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo"
            );
        }
    }

    @Override
    public List<Producto> listarProductosStockBajo() {
        return productoRepository.buscarStockBajo();
    }

    @Override
    public List<Producto> listarProductosSinStock() {
        return productoRepository.buscarSinStock();
    }
}