package com.tienda.gestion.adapter.out.security;

import com.tienda.gestion.adapter.out.persistence.UsuarioJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;
import org.springframework.transaction.annotation.Transactional;

// Busca los usuarios y proporciona sus datos a Spring Security.
@Service
public class UsuarioDetailsService implements UserDetailsService {

    // Repositorio para consultar los usuarios registrados.
    private final UsuarioJpaRepository usuarios;

    // Recibe el repositorio de usuarios.
    public UsuarioDetailsService(UsuarioJpaRepository usuarios) {
        this.usuarios = usuarios;
    }

    // Busca un usuario por su nombre de usuario, solo para lectura.
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        // Busca al usuario; si no existe, lanza un error de autenticación.
        var usuario = usuarios.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        // Devuelve los datos necesarios para validar el acceso y sus permisos.
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isEnabled())
                .build();
    }
}
