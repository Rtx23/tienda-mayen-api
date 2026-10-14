```java
package com.tienda.gestion.application.port.out;

import com.tienda.gestion.domain.model.Producto;
import java.util.List;
import java.util.Optional;

// Define las operaciones para acceder a los productos.
public interface ProductoRepository {

    // Guarda un producto nuevo.
    Producto guardar(Producto producto);

    // Devuelve todos los productos registrados.
    List<Producto> listarTodos();

    // Busca un producto por su identificador.
    Optional<Producto> buscarPorId(Long id);

    // Busca un producto para actualizarlo; por defecto usa la búsqueda normal.
    default Optional<Producto> buscarPorIdParaActualizar(Long id) {
        return buscarPorId(id);
    }

    // Guarda los cambios de un producto existente.
    Producto actualizar(Producto producto);

    // Elimina un producto por su identificador.
    void eliminar(Long id);

    // Busca los productos que tienen stock bajo.
    List<Producto> buscarStockBajo();

    // Busca los productos que no tienen existencias.
    List<Producto> buscarSinStock();
}
```