package com.tienda.gestion.adapter.in.web;

import com.tienda.gestion.application.port.in.ProductoUseCase;
import com.tienda.gestion.domain.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoUseCase productoUseCase;

    public ProductoController(ProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoUseCase.crearProducto(producto);
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoUseCase.listarProductos();
    }

    @GetMapping("/{id}")
    public Producto buscarProductoPorId(@PathVariable Long id) {

        return productoUseCase.buscarProductoPorId(id);
    }
    @PutMapping("/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        return productoUseCase.actualizarProducto(id, producto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoUseCase.eliminarProducto(id);
    }
    @GetMapping("/stock-bajo")
    public List<Producto> listarProductosStockBajo() {

        return productoUseCase.listarProductosStockBajo();
    }
    @GetMapping("/sin-stock")
    public List<Producto> listarProductosSinStock() {

        return productoUseCase.listarProductosSinStock();
    }
}
