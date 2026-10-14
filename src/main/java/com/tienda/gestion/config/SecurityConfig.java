package com.tienda.gestion.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Configura la seguridad y los permisos de la API.
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Encripta las contraseñas de los usuarios.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Comprueba las credenciales cuando un usuario inicia sesión.
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService users, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    // Crea la clave que se utiliza para firmar los tokens JWT.
    @Bean
    SecretKey jwtKey(@Value("${app.jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) throw new IllegalArgumentException("JWT_SECRET requiere al menos 32 bytes");
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    // Genera los tokens JWT con la clave configurada.
    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    // Valida la firma, el algoritmo y el emisor de los tokens recibidos.
    @Bean
    JwtDecoder jwtDecoder(SecretKey key, @Value("${app.jwt.issuer}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    // Define las reglas de acceso para los endpoints de la API.
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        // Lee los roles guardados en el token para asignar los permisos.
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);

        http.csrf(csrf -> csrf.disable())
            .cors(cors -> {})

            // No guarda sesiones porque la API utiliza tokens JWT.
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(b -> b.disable())
            .formLogin(f -> f.disable())
            .logout(l -> l.disable())

            // Permite o restringe las operaciones según el rol del usuario.
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/public/ping").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").hasAnyRole("LECTOR", "EDITOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/productos").hasAnyRole("EDITOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/*").hasAnyRole("EDITOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/ventas", "/api/ventas/*").hasAnyRole("EDITOR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/ventas").hasAnyRole("EDITOR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/datos-comerciales").hasRole("ADMIN")
                .anyRequest().denyAll())

            // Devuelve respuestas controladas cuando falta autenticación o permiso.
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> SecurityErrors.write(res, 401))
                .accessDeniedHandler((req, res, ex) -> SecurityErrors.write(res, 403)))

            // Comprueba el JWT enviado en las solicitudes protegidas.
            .oauth2ResourceServer(o -> o
                .jwt(j -> j.jwtAuthenticationConverter(converter))
                .authenticationEntryPoint((req, res, ex) -> SecurityErrors.write(res, 401))
                .accessDeniedHandler((req, res, ex) -> SecurityErrors.write(res, 403)));

        return http.build();
    }

    // Define qué orígenes pueden conectarse a la API.
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origins}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());

        // Limita los métodos y encabezados permitidos en las solicitudes.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Location"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}