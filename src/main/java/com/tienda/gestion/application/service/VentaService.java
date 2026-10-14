package com.tienda.gestion.application.service;

import com.tienda.gestion.application.exception.*;
import com.tienda.gestion.application.port.in.VentaUseCase;
import com.tienda.gestion.application.port.out.*;
import com.tienda.gestion.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con las ventas.
 *
 * Implementa los casos de uso de VentaUseCase y coordina las operaciones
 * entre los productos y el repositorio de ventas.
 *
 * Permite registrar ventas, consultar las ventas existentes y buscar
 * una venta por su identificador.
 */
@Service
@Transactional(readOnly = true)
@PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
public class VentaService implements VentaUseCase {

    /** Repositorio utilizado para consultar y actualizar productos. */
    private final ProductoRepository productos;

    /** Repositorio encargado de almacenar y consultar las ventas. */
    private final VentaRepository ventas;

    /**
     * Construye el servicio con los repositorios necesarios.
     *
     * @param productos repositorio para gestionar los productos
     * @param ventas repositorio para gestionar las ventas
     */
    public VentaService(ProductoRepository productos, VentaRepository ventas) {
        this.productos = productos;
        this.ventas = ventas;
    }

    /**
     * Registra una venta, actualiza el stock de los productos y calcula
     * el importe total de la operación.
     *
     * Solo los usuarios con rol EDITOR o ADMIN pueden acceder al servicio.
     * La operación se ejecuta dentro de una transacción.
     *
     * @param items lista de productos y cantidades que forman la venta
     * @param usuario nombre del usuario que registra la venta
     * @return venta registrada con sus detalles y total calculado
     * @throws IllegalArgumentException si la lista o sus elementos son inválidos
     * @throws ProductoNoEncontradoException si alguno de los productos no existe
     * @throws ConflictoInventarioException si no hay stock suficiente
     */
    @Transactional
    public Venta registrar(List<Venta.Item> items, String usuario) {

        // La venta debe contener entre uno y cien productos.
        if (items == null || items.isEmpty() || items.size() > 100) {
            throw new IllegalArgumentException("La venta requiere entre 1 y 100 productos");
        }

        // Permite comprobar que un mismo producto no se repita en la venta.
        Set<Long> ids = new HashSet<>();

        for (Venta.Item item : items) {

            // Cada elemento debe tener un producto válido y una cantidad positiva.
            if (item == null || item.productoId() == null || item.productoId() <= 0 || item.cantidad() <= 0) {
                throw new IllegalArgumentException("Producto y cantidad deben ser positivos");
            }

            // Evita registrar dos líneas para el mismo producto.
            if (!ids.add(item.productoId())) {
                throw new IllegalArgumentException("No repetir productos en una venta");
            }
        }

        // Almacena los detalles calculados de la venta.
        List<Venta.Detalle> detalles = new ArrayList<>();

        // Acumula el importe total de todos los productos vendidos.
        BigDecimal total = BigDecimal.ZERO;

        // Bloquear siempre en el mismo orden evita ciclos de bloqueo entre ventas simultáneas.
        for (Venta.Item item : items.stream().sorted(Comparator.comparing(Venta.Item::productoId)).toList()) {

            // Busca el producto y lo bloquea para evitar modificaciones simultáneas del stock.
            Producto producto = productos.buscarPorIdParaActualizar(item.productoId())
                    .orElseThrow(() -> new ProductoNoEncontradoException(item.productoId()));

            // Comprueba que exista suficiente inventario para completar la venta.
            if (producto.getStock() < item.cantidad()) {
                throw new ConflictoInventarioException("Stock insuficiente para " + producto.getNombre());
            }

            // Convierte el precio a BigDecimal y lo expresa con dos decimales.
            BigDecimal precio = BigDecimal.valueOf(producto.getPrecio()).setScale(2);

            // Calcula el subtotal multiplicando el precio por la cantidad vendida.
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.cantidad()));

            // Descuenta del inventario las unidades vendidas.
            producto.setStock(producto.getStock() - item.cantidad());
            productos.actualizar(producto);

            // Guarda en el detalle los datos del producto y los importes de la venta.
            detalles.add(new Venta.Detalle(producto.getId(), producto.getNombre(), item.cantidad(), precio, subtotal));

            // Añade el subtotal al total de la venta.
            total = total.add(subtotal);
        }

        // Registra la venta con su fecha, usuario, total y detalles.
        return ventas.guardar(new Venta(null, LocalDateTime.now(), usuario, total, List.copyOf(detalles)));
    }

    /**
     * Obtiene todas las ventas registradas.
     *
     * @return lista de ventas almacenadas
     */
    public List<Venta> listar() {
        return ventas.listar();
    }

    /**
     * Busca una venta mediante su identificador.
     *
     * @param id identificador de la venta
     * @return venta encontrada
     * @throws VentaNoEncontradaException si no existe una venta con ese identificador
     */
    public Venta buscar(Long id) {
        return ventas.buscar(id)
                .orElseThrow(() -> new VentaNoEncontradaException(id));
    }
}