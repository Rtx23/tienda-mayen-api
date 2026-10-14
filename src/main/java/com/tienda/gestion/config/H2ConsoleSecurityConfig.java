package com.tienda.gestion.config;

import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Configuración de seguridad para la consola H2.
@Configuration
@Profile("consola")
public class H2ConsoleSecurityConfig {

    // Define las reglas de acceso a la consola H2.
    @Bean
    @Order(1)
    SecurityFilterChain consolaSecurity(HttpSecurity http) throws Exception {

        // Controla la respuesta cuando el usuario no ha iniciado sesión.
        AuthenticationEntryPoint accesoConsola = (request, response, exception) -> {
            String accept = request.getHeader("Accept");

            // Si es una página web, redirige al login.
            if (accept != null && accept.contains("text/html")) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                // Si es otra petición, devuelve un error 401 con un mensaje.
                response.setStatus(401);
                response.setHeader("WWW-Authenticate", "Basic realm=\"Tienda MAYEN H2\"");
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Inicia sesión en /login para acceder a la consola H2");
            }
        };

        // Aplica estas reglas a la consola H2 y al inicio de sesión.
        http.securityMatcher("/h2-console", "/h2-console/**", "/login")

            // Permite el login y exige el rol ADMIN para las demás rutas.
            .authorizeHttpRequests(a -> a.requestMatchers("/login").permitAll().anyRequest().hasRole("ADMIN"))

            // Configura el formulario de inicio de sesión.
            .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/h2-console/", true).permitAll())

            // Permite autenticación básica y define cómo responder si falla.
            .httpBasic(b -> b.authenticationEntryPoint(accesoConsola))
            .exceptionHandling(e -> e.authenticationEntryPoint(accesoConsola))

            // Desactiva CSRF para esta consola.
            .csrf(csrf -> csrf.disable())

            // Permite mostrar la consola dentro de un iframe del mismo origen.
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        // Devuelve la configuración de seguridad.
        return http.build();
    }
}