package com.tienda.gestion.adapter.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tienda.gestion.application.port.out.ProductoRepository;
import com.tienda.gestion.domain.model.Producto;

@Repository
public class ProductoInMemoryRepository implements ProductoRepository {

    // Lista que almacena los productos temporalmente en memoria.
    private final List<Producto> productos = new ArrayList<>();

    // Genera automáticamente los identificadores de los productos.
    private Long contador = 1L;

    @Override
    public Producto guardar(Producto producto) {

        // Asigna un ID único al nuevo producto.
        producto.setId(contador);
        contador++;

        productos.add(producto);
        return producto;
    }

    @Override
    public List<Producto> listarTodos() {
        return productos;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {

        // Busca el producto cuyo ID coincida con el solicitado.
        return productos.stream()
                .filter(producto -> producto.getId().equals(id))
                .findFirst();
    }

    @Override
    public Producto actualizar(Producto producto) {

        // Reemplaza el producto existente por sus nuevos datos.
        for (int i = 0; i < productos.size(); i++) {

            if (productos.get(i).getId().equals(producto.getId())) {
                productos.set(i, producto);
                return producto;
            }
        }

        return null;
    }

    @Override
    public void eliminar(Long id) {

        // Elimina el producto que tenga el ID indicado.
        productos.removeIf(
                producto -> producto.getId().equals(id)
        );
    }

    @Override
    public List<Producto> buscarStockBajo() {

        // Obtiene productos cuyo stock está por debajo del mínimo establecido.
        return productos.stream()
                .filter(producto ->
                        producto.getStock() <= producto.getStockMinimo())
                .toList();
    }

    @Override
    public List<Producto> buscarSinStock() {

        // Obtiene productos que tienen stock igual a cero.
        return productos.stream()
                .filter(producto -> producto.getStock() == 0)
                .toList();
    }
}