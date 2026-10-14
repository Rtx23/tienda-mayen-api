package com.tienda.gestion.application.service;

import com.tienda.gestion.application.exception.ProductoNoEncontradoException;
import com.tienda.gestion.application.port.in.ProductoUseCase;
import com.tienda.gestion.application.port.out.ProductoRepository;
import com.tienda.gestion.domain.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones de productos.
 *
 * Implementa los casos de uso definidos en ProductoUseCase,
 * delega las operaciones de persistencia al repositorio y aplica
 * validaciones antes de crear o actualizar productos.
 *
 * Los métodos están protegidos según el rol del usuario autenticado.
 */
@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('LECTOR', 'EDITOR', 'ADMIN')")
public class ProductoService implements ProductoUseCase {

    /** Repositorio utilizado para consultar y gestionar productos. */
    private final ProductoRepository productoRepository;

    /**
     * Construye el servicio con el repositorio de productos.
     *
     * @param productoRepository repositorio para acceder a los productos
     */
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Crea un producto después de validar sus datos.
     *
     * Solo los usuarios con rol EDITOR o ADMIN pueden ejecutar esta operación.
     *
     * @param producto producto que se desea registrar
     * @return producto guardado
     * @throws IllegalArgumentException si los datos del producto son inválidos
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public Producto crearProducto(Producto producto) {

        validarProducto(producto);

        // El identificador de una creación lo genera la base de datos.
        producto.setId(null);

        return productoRepository.guardar(producto);
    }

    /**
     * Obtiene todos los productos registrados.
     *
     * @return lista de productos disponibles
     */
    @Override
    public List<Producto> listarProductos() {
        return productoRepository.listarTodos();
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto
     * @return producto encontrado
     * @throws ProductoNoEncontradoException si el producto no existe
     */
    @Override
    public Producto buscarProductoPorId(Long id) {

        return productoRepository.buscarPorId(id)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(id)
                );
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * Solo los usuarios con rol EDITOR o ADMIN pueden ejecutar esta operación.
     *
     * @param id identificador del producto que se actualizará
     * @param producto nuevos datos del producto
     * @return producto actualizado
     * @throws ProductoNoEncontradoException si el producto no existe
     * @throws IllegalArgumentException si los datos del producto son inválidos
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public Producto actualizarProducto(Long id, Producto producto) {

        // Comprueba que el producto exista antes de actualizarlo.
        productoRepository.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));

        validarProducto(producto);

        // Conserva el identificador del producto que se está actualizando.
        producto.setId(id);

        return productoRepository.actualizar(producto);
    }

    /**
     * Elimina un producto existente.
     *
     * Esta operación requiere el rol ADMIN.
     *
     * @param id identificador del producto que se eliminará
     * @throws ProductoNoEncontradoException si el producto no existe
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarProducto(Long id) {

        // Verifica que el producto exista antes de eliminarlo.
        productoRepository.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));

        productoRepository.eliminar(id);
    }

    /**
     * Comprueba que los datos del producto cumplan las reglas de validación.
     *
     * Valida el nombre, el precio, el stock, la fecha de vencimiento
     * y el stock mínimo.
     *
     * @param producto producto cuyos datos serán validados
     * @throws IllegalArgumentException si algún dato no cumple las reglas
     */
    private void validarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }

        if (producto.getNombre() == null ||
            producto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio"
            );
        }

        if (producto.getNombre().length() > 120) {
            throw new IllegalArgumentException("El nombre admite hasta 120 caracteres");
        }

        if (!Double.isFinite(producto.getPrecio()) || producto.getPrecio() <= 0
                || producto.getPrecio() > 9999999999.99
                || BigDecimal.valueOf(producto.getPrecio()).stripTrailingZeros().scale() > 2) {

            throw new IllegalArgumentException(
                    "El precio debe ser positivo, con hasta 10 enteros y 2 decimales"
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

    /**
     * Obtiene los productos cuyo stock se encuentra por debajo del mínimo.
     *
     * @return lista de productos con stock bajo
     */
    @Override
    public List<Producto> listarProductosStockBajo() {

        return productoRepository.buscarStockBajo();
    }

    /**
     * Obtiene los productos que no tienen unidades disponibles.
     *
     * @return lista de productos sin stock
     */
    @Override
    public List<Producto> listarProductosSinStock() {

        return productoRepository.buscarSinStock();
    }
}