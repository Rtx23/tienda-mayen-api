package com.tienda.gestion.adapter.in.web;

import com.tienda.gestion.application.port.in.ProductoUseCase;
import com.tienda.gestion.domain.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ProductoControllerTest {

    private MockMvc mockMvc;

    // Simula el caso de uso para probar el controlador de forma aislada.
    @Mock
    private ProductoUseCase productoUseCase;

    @BeforeEach
    void configurar() {
        MockitoAnnotations.openMocks(this);

        ProductoController controller =
                new ProductoController(productoUseCase);

        // Configura MockMvc para realizar pruebas sobre el controlador.
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // Verifica que la creación de un producto retorne HTTP 201.
    @Test
    void crearProducto_debeRetornar201() throws Exception {

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Inca Kola");
        producto.setPrecio(3.50);
        producto.setStock(20);

        when(productoUseCase.crearProducto(org.mockito.ArgumentMatchers.any(Producto.class)))
                .thenReturn(producto);

        String json = """
                {
                    "id": 1,
                    "nombre": "Inca Kola",
                    "precio": 3.50,
                    "stock": 20,
                    "fechaVencimiento": "2026-12-15"
                }
                """;

        mockMvc.perform(
                post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.nombre").value("Inca Kola"));
    }

    // Verifica que la búsqueda de un producto inexistente retorne HTTP 404.
    @Test
    void buscarProductoInexistente_debeRetornar404() throws Exception {

        when(productoUseCase.buscarProductoPorId(999L))
                .thenReturn(null);

        mockMvc.perform(
                get("/api/productos/999")
        )
        .andExpect(status().isNotFound());
    }

    // Verifica que la consulta de productos con stock bajo retorne HTTP 200.
    @Test
    void stockBajo_debeRetornar200() throws Exception {

        when(productoUseCase.listarProductosStockBajo())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/productos/stock-bajo")
        )
        .andExpect(
                status().isOk()
        );
    }

    // Verifica que la consulta de productos sin stock retorne HTTP 200.
    @Test
    void sinStock_debeRetornar200() throws Exception {

        when(productoUseCase.listarProductosSinStock())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/productos/sin-stock")
        )
        .andExpect(
                status().isOk()
        );
    }
}