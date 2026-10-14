package com.tienda.gestion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de seguridad de la consola H2.
 *
 * Verifica que el acceso a la consola esté protegido, que los usuarios
 * respeten los permisos asignados y que la autenticación web no conceda
 * automáticamente acceso a los endpoints de la API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"demo", "consola"})
class H2ConsoleSecurityTest {

    @Autowired
    MockMvc mvc;

    /**
     * Comprueba que la consola rechace las solicitudes no autenticadas
     * y que un usuario sin privilegios de administrador no pueda acceder.
     *
     * También verifica que las credenciales Basic no sustituyan el
     * mecanismo de autenticación requerido por la API.
     */
    @Test
    void consolaRequiereAutenticacionYRolAdmin() throws Exception {

        // Sin credenciales, el acceso a la consola debe solicitar autenticación.
        mvc.perform(get("/h2-console/"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        "WWW-Authenticate",
                        "Basic realm=\"Tienda MAYEN H2\""
                ));

        // El usuario lector está autenticado, pero no tiene permisos de administrador.
        mvc.perform(get("/h2-console/")
                        .with(httpBasic("lector", "lector123")))
                .andExpect(status().isForbidden());

        /*
         * MockMvc no ejecuta el servlet real de la consola H2.
         * Por eso, aquí se verifica la respuesta de seguridad de la aplicación,
         * no el funcionamiento interno de la interfaz de H2.
         */
        mvc.perform(get("/api/productos")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Comprueba el flujo de autenticación web mediante formulario
     * y verifica que la sesión obtenida no autorice por sí sola
     * las solicitudes a la API.
     */
    @Test
    void navegadorPuedeEntrarMedianteFormularioSinAutenticarLaApi()
            throws Exception {

        // Un navegador sin sesión debe ser redirigido al formulario de acceso.
        mvc.perform(get("/h2-console/")
                        .header("Accept", "text/html"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/login"));

        // El formulario de inicio de sesión debe estar disponible.
        mvc.perform(get("/login"))
                .andExpect(status().isOk());

        // Se envían las credenciales y se comprueba la redirección tras el acceso.
        var result = mvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/h2-console/"))
                .andReturn();

        MockHttpSession session =
                (MockHttpSession) result.getRequest().getSession(false);

        /*
         * La sesión del formulario web no debe considerarse suficiente
         * para autenticar la API, que tiene su propio mecanismo de seguridad.
         */
        mvc.perform(get("/api/productos")
                        .session(session))
                .andExpect(status().isUnauthorized());
    }
}