package com.tienda.gestion.adapter.in.web;

import com.tienda.gestion.application.port.in.ProductoUseCase;
import com.tienda.gestion.domain.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;

/**
 * Controlador REST encargado de gestionar las solicitudes HTTP
 * relacionadas con los productos de la tienda.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    // Interfaz que permite acceder a las operaciones de productos.
    private final ProductoUseCase productoUseCase;

    /**
     * Inicializa el controlador con el caso de uso de productos.
     *
     * @param productoUseCase servicio que ejecuta las operaciones de productos
     */
    public ProductoController(ProductoUseCase productoUseCase) {
        this.productoUseCase = productoUseCase;
    }

    /**
     * Registra un producto nuevo.
     *
     * @param request datos del producto recibidos en la solicitud
     * @return respuesta HTTP 201 con la ubicación y los datos del producto creado
     */
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequest request) {
        // Convierte la solicitud al modelo de dominio y crea el producto.
        Producto producto = productoUseCase.crearProducto(request.dominio());

        // Devuelve la ubicación del recurso recién creado.
        return ResponseEntity.created(URI.create("/api/productos/" + producto.getId())).body(producto);
    }

    /**
     * Obtiene la lista de productos registrados.
     *
     * @return lista de productos
     */
    @GetMapping
    public List<Producto> listarProductos() {
        return productoUseCase.listarProductos();
    }

    /**
     * Busca un producto mediante su identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     */
    @GetMapping("/{id}")
    public Producto buscarProductoPorId(@PathVariable Long id) {
        return productoUseCase.buscarProductoPorId(id);
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id identificador del producto que se actualizará
     * @param producto datos nuevos del producto
     * @return producto actualizado
     */
    @PutMapping("/{id}")
    public Producto actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest producto) {

        return productoUseCase.actualizarProducto(id, producto.dominio());
    }

    /**
     * Elimina un producto mediante su identificador.
     *
     * @param id identificador del producto que se eliminará
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoUseCase.eliminarProducto(id);
    }

    /**
     * Obtiene los productos cuyo stock se encuentra por debajo
     * del nivel establecido por la lógica de la aplicación.
     *
     * @return lista de productos con stock bajo
     */
    @GetMapping("/stock-bajo")
    public List<Producto> listarProductosStockBajo() {
        return productoUseCase.listarProductosStockBajo();
    }

    /**
     * Obtiene los productos que no tienen unidades disponibles.
     *
     * @return lista de productos sin stock
     */
    @GetMapping("/sin-stock")
    public List<Producto> listarProductosSinStock() {
        return productoUseCase.listarProductosSinStock();
    }
}