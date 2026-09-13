package com.tienda.gestion.adapter.in.web;

import com.tienda.gestion.application.port.in.ProductoUseCase;
import com.tienda.gestion.domain.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    // Caso de uso encargado de gestionar las operaciones de productos.
    private final ProductoUseCase productoUseCase;

    public ProductoController(ProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    // Registra un nuevo producto.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoUseCase.crearProducto(producto);
    }

    // Obtiene todos los productos registrados.
    @GetMapping
    public List<Producto> listarProductos() {
        return productoUseCase.listarProductos();
    }

    // Busca un producto por su identificador.
    @GetMapping("/{id}")
    public Producto buscarProductoPorId(@PathVariable Long id) {
        return productoUseCase.buscarProductoPorId(id);
    }

    // Actualiza los datos de un producto existente.
    @PutMapping("/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        return productoUseCase.actualizarProducto(id, producto);
    }

    // Elimina un producto por su identificador.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoUseCase.eliminarProducto(id);
    }

    // Lista los productos cuyo stock está por debajo del mínimo.
    @GetMapping("/stock-bajo")
    public List<Producto> listarProductosStockBajo() {
        return productoUseCase.listarProductosStockBajo();
    }

    // Lista los productos que actualmente no tienen stock.
    @GetMapping("/sin-stock")
    public List<Producto> listarProductosSinStock() {
        return productoUseCase.listarProductosSinStock();
    }
}