package com.snikers.shop.config; // Esta es la carpeta virtual donde guardamos las reglas de seguridad.

// Traemos instrucciones prefabricadas de la herramienta (Spring)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.snikers.shop.service.UserService; // Nuestro listado de usuarios del sistema

/**
 * Este archivo es como el "Vigilante de Seguridad" del centro comercial.
 * Revisa el DNI de la gente y decide a qué pasillos pueden entrar.
 * 
 * Configuración de seguridad:
 * - El pasillo principal, escaparates (catálogo) y puertas de entrada/registro: públicos.
 * - La zona vip o sala de mandos (/admin) solo con tarjeta "ADMIN".
 * - Pagar en caja (/checkout), ver historial (/orders) requiere estar registrado.
 */
@Configuration // Avisamos al programa de que aquí hay reglas importantes que debe leer al encender.
public class SecurityConfig {

    // "La trituradora de contraseñas". Nadie, ni nosotros, debe saber las contraseñas.
    // Esto convierte "1234" en un texto raro como "$2a$10$vNlA..." para que no nos las roben.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // "El comprobador de DNI".
    // Coge el usuario que le pasamos, busca en su lista (UserService) si existe,
    // y comprueba si la contraseña introducida coincide usando nuestra trituradora de contraseñas.
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // "El jefe de seguridad". El que da el visto bueno final sobre si alguien está autenticado.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // "El filtro de la puerta". Aquí decidimos estrictamente qué reglas se aplican a cada pasillo de la tienda.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desactivamos ciertas protecciones complejas para facilitar el desarrollo académico
                .authorizeHttpRequests(auth -> auth
                        // 1. Zonas públicas (cualquiera puede pasar, incluso sin iniciar sesión)
                        // Incluye imágenes, botones de CSS, portada, etc.
                        .requestMatchers(
                                "/", "/home", "/products/**", "/categories/**",
                                "/search", "/register", "/login",
                                "/css/**", "/js/**", "/img/**", "/webjars/**",
                                "/h2-console/**", "/api/public/**"
                        ).permitAll()
                        // 2. Zona de empleados. Pide expresamente el rol "ADMIN"
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        // 3. El carrito. De momento dejamos que la gente meta cosas sin registrarse.
                        .requestMatchers("/cart/**").permitAll()
                        // 4. Secciones privadas. Pagar y ver el panel de tu perfil exige registrarse primero.
                        .requestMatchers("/checkout/**", "/profile/**", "/orders/**").authenticated()
                        // 5. Por si se nos olvida alguna página, la dejamos pública.
                        .anyRequest().permitAll()
                )
                // "El diseño del formulario de inicio de sesión"
                .formLogin(form -> form
                        .loginPage("/login") // La dirección de la página login en nuestra web
                        .loginProcessingUrl("/login") // Donde se envían los datos cuando pulsas "Entrar"
                        .usernameParameter("email") // Lo que pedimos para iniciar sesión es el email
                        .passwordParameter("password") // Y la contraseña
                        .defaultSuccessUrl("/", false) // Si todo va bien, redirige a la página solicitada (o "/" por defecto)
                        .failureUrl("/login?error=true") // Si fallan datos, enseñamos un error
                        .permitAll() // Permitimos que la ventana sea de acceso público
                )
                // "El botón de salir / cerrar sesión"
                .logout(logout -> logout
                        .logoutUrl("/logout") // A donde tenemos que ir para salir
                        .logoutSuccessUrl("/?logout=true") // A donde te lleva tras salir de tu cuenta (al inicio)
                        .invalidateHttpSession(true) // Destruye temporalmente todos los datos de tu sesión (ya no estas activo)
                        .deleteCookies("JSESSIONID") // Borra el 'sello de mano' virtual que nos dieron al entrar
                        .permitAll()
                )
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin())); // permite H2 console (Herramienta técnica para ver la base de datos)

        return http.build();
    }
}
