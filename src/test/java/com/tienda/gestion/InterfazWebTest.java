package com.tienda.gestion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class InterfazWebTest {
    @Autowired MockMvc mvc;

    @Test void interfazPublicaNoExponeLaApiSinToken() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("index.html"));
        mvc.perform(get("/index.html")).andExpect(status().isOk()).andExpect(content().string(containsString("<app-root>")));
        mvc.perform(get("/assets/angular/manifest.json")).andExpect(status().isOk()).andExpect(content().string(containsString("Angular")));
        mvc.perform(get("/assets/images/bienvenida.png")).andExpect(status().isOk()).andExpect(content().contentType("image/png"));
        mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/ventas")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/admin/datos-comerciales")).andExpect(status().isUnauthorized());
    }
}
