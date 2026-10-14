package com.tienda.gestion;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import com.jayway.jsonpath.JsonPath;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:limpieza_aislada;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false",
    "app.jwt.secret=clave-jwt-solo-pruebas-aisladas-32-bytes-minimo",
    "app.bootstrap.enabled=true", "app.bootstrap.admin-password=admin123",
    "app.bootstrap.editor-password=editor123", "app.bootstrap.lector-password=lector123"
})
@AutoConfigureMockMvc @ActiveProfiles("demo")
class LimpiezaIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    static final String URL = "/api/admin/datos-comerciales";
    static final String CONFIRMACION = "ELIMINAR_DATOS_COMERCIALES";

    @BeforeEach void datos() {
        jdbc.update("delete from detalles_venta");
        jdbc.update("delete from ventas");
        jdbc.update("delete from productos");
        jdbc.update("insert into productos(id,nombre,precio,stock,stock_minimo,fecha_vencimiento) values (9001,'Producto prueba',5.50,8,2,'2028-12-31')");
        jdbc.update("insert into ventas(id,fecha,usuario,total) values (9001,CURRENT_TIMESTAMP,'editor',11.00)");
        jdbc.update("insert into detalles_venta(id,venta_id,producto_id,nombre_producto,cantidad,precio_unitario,subtotal) values (9001,9001,9001,'Producto prueba',2,5.50,11.00)");
    }
    List<Map<String,Object>> usuarios() { return jdbc.queryForList("select * from usuarios order by id"); }
    String login(String usuario) throws Exception {
        String response = mvc.perform(post("/api/auth/login").contentType("application/json")
            .content("{\"username\":\""+usuario+"\",\"password\":\""+usuario+"123\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.accessToken");
    }
    void cantidades(int esperado) {
        for (String t : List.of("productos", "ventas", "detalles_venta")) {
            assertEquals(esperado, jdbc.queryForObject("select count(*) from " + t, Integer.class));
        }
    }
    @Test
    void borraDatosYConservaCadaCampoDeTodosLosUsuarios() throws Exception {
        var antes = usuarios();
        assertTrue(antes.size() >= 3);
        mvc.perform(delete(URL).header("Authorization", "Bearer " + login("admin")).header("X-Confirmar-Eliminacion", CONFIRMACION))
            .andExpect(status().isOk()).andExpect(jsonPath("$.usuariosConservados").value(true))
            .andExpect(jsonPath("$.detallesEliminados").value(1))
            .andExpect(jsonPath("$.ventasEliminadas").value(1))
            .andExpect(jsonPath("$.productosEliminados").value(1));
        cantidades(0); assertEquals(antes, usuarios());
        for (String u : List.of("admin", "editor", "lector")) {
            mvc.perform(post("/api/auth/login").contentType("application/json")
                .content("{\"username\":\""+u+"\",\"password\":\""+u+"123\"}"))
                .andExpect(status().isOk());
        }
        mvc.perform(delete(URL).header("Authorization", "Bearer " + login("admin")).header("X-Confirmar-Eliminacion", CONFIRMACION))
            .andExpect(status().isOk()).andExpect(jsonPath("$.productosEliminados").value(0));
    }
    @Test
    void exigeConfirmacionYNoModificaDatos() throws Exception {
        mvc.perform(delete(URL).header("Authorization", "Bearer " + login("admin")).header("X-Confirmar-Eliminacion", "NO")).andExpect(status().isBadRequest());
        cantidades(1);
    }
    @Test
    void editorNoPuedeLimpiar() throws Exception {
        mvc.perform(delete(URL).header("Authorization", "Bearer " + login("editor")).header("X-Confirmar-Eliminacion", CONFIRMACION)).andExpect(status().isForbidden());
        cantidades(1);
    }
    @Test
    void lectorNoPuedeLimpiar() throws Exception {
        mvc.perform(delete(URL).header("Authorization", "Bearer " + login("lector")).header("X-Confirmar-Eliminacion", CONFIRMACION)).andExpect(status().isForbidden());
        cantidades(1);
    }
    @Test void sinTokenNoPuedeLimpiar() throws Exception {
        mvc.perform(delete(URL).header("X-Confirmar-Eliminacion", CONFIRMACION)).andExpect(status().isUnauthorized());
        cantidades(1);
    }
    @Test
    void falloEnProductoRevierteVentasYDetalles() throws Exception {
        var antes = usuarios();
        jdbc.execute("create table bloqueo_limpieza(producto_id bigint references productos(id))");
        try {
            jdbc.update("insert into bloqueo_limpieza values (9001)");
            mvc.perform(delete(URL).header("Authorization", "Bearer " + login("admin")).header("X-Confirmar-Eliminacion", CONFIRMACION)).andExpect(status().isConflict());
            cantidades(1); assertEquals(antes, usuarios());
        } finally { jdbc.execute("drop table bloqueo_limpieza"); }
    }
}
