package com.bancoxyz.transaccion.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga usuarios y sus roles.
 * 
 * Usuarios disponibles:
 *   - admin / admin123  → Roles: ADMIN, USER
 *   - user  / user123   → Rol: USER
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("{noop}admin123")
                    .roles("ADMIN", "USER")
                    .build();
        }

        if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("{noop}user123")
                    .roles("USER")
                    .build();
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
