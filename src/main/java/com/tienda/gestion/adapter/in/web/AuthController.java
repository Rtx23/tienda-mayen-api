package com.tienda.gestion.adapter.in.web;

import com.tienda.gestion.adapter.out.security.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

/**
 * Controlador encargado de gestionar la autenticación de los usuarios.
 *
 * Expone el endpoint para iniciar sesión y obtener un token de acceso
 * después de validar las credenciales del usuario.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Componente encargado de autenticar las credenciales del usuario. */
    private final AuthenticationManager authenticationManager;

    /** Servicio responsable de generar los tokens de acceso. */
    private final TokenService tokens;

    /**
     * Construye el controlador con los componentes de autenticación.
     *
     * @param authenticationManager administrador de autenticación
     * @param tokens servicio para generar tokens
     */
    public AuthController(AuthenticationManager authenticationManager, TokenService tokens) {
        this.authenticationManager = authenticationManager;
        this.tokens = tokens;
    }

    /**
     * Representa los datos requeridos para iniciar sesión.
     *
     * @param username nombre de usuario, obligatorio y de hasta 60 caracteres
     * @param password contraseña, obligatoria y de hasta 72 caracteres
     */
    public record LoginRequest(
            @NotBlank @Size(max = 60) String username,
            @NotBlank @Size(max = 72) String password
    ) {}

    /**
     * Autentica al usuario y devuelve un token de acceso.
     *
     * Comprueba que la contraseña no supere los 72 bytes en UTF-8,
     * autentica las credenciales y genera el token correspondiente.
     * La respuesta utiliza una política que evita almacenar la información
     * en caché.
     *
     * @param request datos de inicio de sesión validados
     * @return respuesta HTTP con el token generado
     * @throws IllegalArgumentException si la contraseña supera los 72 bytes
     */
    @PostMapping("/login")
    public ResponseEntity<TokenService.TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        // Comprueba el tamaño real de la contraseña codificada en UTF-8.
        if (request.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("La contraseña excede 72 bytes");
        }

        // Solicita a Spring Security que autentique las credenciales recibidas.
        var auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.username(), request.password()));

        // Devuelve el token sin permitir que la respuesta se almacene en caché.
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(tokens.generar(auth));
    }
}