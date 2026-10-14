package com.tienda.gestion.adapter.out.persistence;

import com.tienda.gestion.application.port.out.VentaRepository;
import com.tienda.gestion.domain.model.Venta;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Implementa las operaciones de ventas utilizando JPA.
@Repository
public class VentaJpaAdapter implements VentaRepository {

    // Repositorios para gestionar las ventas y consultar productos.
    private final VentaJpaRepository ventas;
    private final ProductoJpaRepository productos;

    // Recibe los repositorios necesarios.
    public VentaJpaAdapter(VentaJpaRepository ventas, ProductoJpaRepository productos) {
        this.ventas = ventas;
        this.productos = productos;
    }

    // Guarda una venta con todos sus detalles.
    public Venta guardar(Venta v) {
        VentaEntity entity = new VentaEntity();
        entity.fecha = v.fecha();
        entity.usuario = v.usuario();
        entity.total = v.total();

        // Agrega los productos y sus datos al detalle de la venta.
        for (Venta.Detalle d : v.detalles()) {
            DetalleVentaEntity detalle = new DetalleVentaEntity();
            detalle.venta = entity;
            detalle.producto = productos.getReferenceById(d.productoId());
            detalle.nombreProducto = d.nombreProducto();
            detalle.cantidad = d.cantidad();
            detalle.precioUnitario = d.precioUnitario();
            detalle.subtotal = d.subtotal();
            entity.detalles.add(detalle);
        }

        // Guarda la venta en la base de datos y devuelve el modelo de dominio.
        return ventas.saveAndFlush(entity).dominio();
    }

    // Lista todas las ventas ordenadas por su identificador.
    public List<Venta> listar() {
        return ventas.findAll(org.springframework.data.domain.Sort.by("id"))
                .stream()
                .map(VentaEntity::dominio)
                .toList();
    }

    // Busca una venta por su identificador.
    public Optional<Venta> buscar(Long id) {
        return ventas.findById(id).map(VentaEntity::dominio);
    }
}
