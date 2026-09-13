package com.tienda.gestion.application;

import com.tienda.gestion.application.exception.ProductoNoEncontradoException;
import com.tienda.gestion.application.port.out.ProductoRepository;
import com.tienda.gestion.application.service.ProductoService;
import com.tienda.gestion.domain.model.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // Simula el repositorio para probar el servicio de forma aislada.
    @Mock
    private ProductoRepository productoRepository;

    // Inyecta el repositorio simulado en el servicio.
    @InjectMocks
    private ProductoService productoService;

    // Verifica que un producto válido pueda ser creado correctamente.
    @Test
    void crearProducto_debeGuardarProducto() {

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Inca Kola");
        producto.setPrecio(3.50);
        producto.setStock(20);
        producto.setFechaVencimiento(LocalDate.of(2026, 12, 15));

        when(productoRepository.guardar(producto)).thenReturn(producto);

        Producto resultado = productoService.crearProducto(producto);

        assertNotNull(resultado);
        assertEquals("Inca Kola", resultado.getNombre());
        assertEquals(3.50, resultado.getPrecio());
        assertEquals(20, resultado.getStock());

        verify(productoRepository).guardar(producto);
    }

    // Verifica que se pueda encontrar un producto existente por su ID.
    @Test
    void buscarProductoPorId_cuandoExiste_debeRetornarProducto() {

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Inca Kola");
        producto.setPrecio(3.50);
        producto.setStock(20);

        when(productoRepository.buscarPorId(1L))
                .thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarProductoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Inca Kola", resultado.getNombre());

        verify(productoRepository).buscarPorId(1L);
    }

    // Verifica que se lance una excepción cuando el producto no existe.
    @Test
    void buscarProductoPorId_cuandoNoExiste_debeLanzarExcepcion() {

        when(productoRepository.buscarPorId(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.buscarProductoPorId(999L)
        );
    }

    // Verifica que no se permita crear productos con precio negativo.
    @Test
    void crearProducto_conPrecioNegativo_debeLanzarExcepcion() {

        Producto productoInvalido = new Producto();

        productoInvalido.setNombre("Producto prueba");
        productoInvalido.setPrecio(-10);
        productoInvalido.setStock(5);
        productoInvalido.setStockMinimo(2);
        productoInvalido.setFechaVencimiento(
                LocalDate.of(2027, 1, 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> productoService.crearProducto(productoInvalido)
        );
    }

    // Verifica que se obtengan correctamente los productos con stock bajo.
    @Test
    void listarProductosStockBajo_debeRetornarProductos() {

        Producto producto = new Producto();

        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(5.50);
        producto.setStock(2);
        producto.setStockMinimo(5);
        producto.setFechaVencimiento(
                LocalDate.of(2026, 12, 20)
        );

        when(productoRepository.buscarStockBajo())
                .thenReturn(List.of(producto));

        List<Producto> resultado =
                productoService.listarProductosStockBajo();

        assertEquals(1, resultado.size());

        assertEquals(
                "Leche",
                resultado.get(0).getNombre()
        );
    }

    // Verifica que se obtengan correctamente los productos sin stock.
    @Test
    void listarProductosSinStock_debeRetornarProductosAgotados() {

        Producto producto = new Producto();

        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(4.80);
        producto.setStock(0);
        producto.setStockMinimo(5);
        producto.setFechaVencimiento(
                LocalDate.of(2027, 5, 20)
        );

        when(productoRepository.buscarSinStock())
                .thenReturn(List.of(producto));

        List<Producto> resultado =
                productoService.listarProductosSinStock();

        assertEquals(1, resultado.size());

        assertEquals(
                0,
                resultado.get(0).getStock()
        );
    }
}