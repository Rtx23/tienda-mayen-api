package com.tienda.gestion.application.port.in;

import com.tienda.gestion.domain.model.Venta;
import java.util.List;

/**
 * Define los casos de uso disponibles para gestionar las ventas.
 *
 * Establece las operaciones que puede realizar la aplicación
 * para registrar y consultar ventas.
 */
public interface VentaUseCase {

    /**
     * Registra una venta con los productos y cantidades indicados.
     *
     * @param items lista de productos y cantidades que forman la venta
     * @param usuario nombre del usuario que registra la venta
     * @return venta registrada con sus detalles
     */
    Venta registrar(List<Venta.Item> items, String usuario);

    /**
     * Obtiene la lista de todas las ventas registradas.
     *
     * @return lista de ventas
     */
    List<Venta> listar();

    /**
     * Busca una venta mediante su identificador.
     *
     * @param id identificador de la venta que se desea consultar
     * @return venta encontrada
     */
    Venta buscar(Long id);
}