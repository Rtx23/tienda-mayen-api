package com.tienda.gestion.application.port.out;

import com.tienda.gestion.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Producto guardar(Producto producto);

    List<Producto> listarTodos();

    Optional<Producto> buscarPorId(Long id);

    Producto actualizar(Producto producto);

    void eliminar(Long id);
	
	List<Producto> buscarStockBajo();
	
	List<Producto> buscarSinStock();
}
