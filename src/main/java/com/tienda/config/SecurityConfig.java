package com.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 🎓 CONFIGURACIÓN DE SEGURIDAD: SecurityConfig
 * Esta clase es como el "portero de la discoteca" de nuestra aplicación web.
 * Aquí definimos quiénes pueden entrar, qué páginas son públicas y cuáles
 * requieren permisos especiales (como ser Administrador).
 */
@Configuration // Indica a Spring que esta clase es de configuración y debe cargarla al arrancar.
@EnableWebSecurity // Activa la magia de la seguridad web (Spring Security) en el proyecto.
public class SecurityConfig {

    /**
     * @Bean: Significa que este método construye un objeto que va a vivir en el contexto
     * de Spring y que será usado automáticamente para configurar la aplicación.
     * 
     * SecurityFilterChain es la cadena de "filtros" o reglas por las que pasa toda petición web.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF (falsificación de petición) para simplificar en temas académicos
            .authorizeHttpRequests(auth -> auth
                // Rutas que son completamente públicas (cualquiera puede ver la portada, productos o fotos)
                .requestMatchers("/", "/home", "/productos", "/carrito/**", "/images/**", "/css/**", "/js/**", "/uploads/**").permitAll()
                
                // Rutas privadas: SOLO un usuario con el rol (permiso) "ADMIN" puede crear, editar o borrar productos
                .requestMatchers("/productos/nuevo", "/productos/guardar", "/productos/editar/**", "/productos/eliminar/**", "/clientes/**").hasRole("ADMIN")
                
                // Cualquier otra página que no esté en la lista de arriba requerirá que inicies sesión
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login") // Le indicamos a Spring Security cuál es nuestra página HTML para iniciar sesión
                .loginProcessingUrl("/login") // URL interna de Spring Security para procesar el usuario y contraseña del formulario
                .permitAll() // Permitir a cualquiera ver el formulario de login
                .defaultSuccessUrl("/", true) // Si el login es correcto, llévalo al inicio (home)
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL en la que haces clic para salir de tu cuenta
                .logoutSuccessUrl("/") // Al salir, llévalo al inicio
                .permitAll()
            );
            
        return http.build();
    }

    /**
     * Configuración de usuarios en memoria (HARDCODEADOS o fijos) para el proyecto.
     * En el mundo real se conectarían a una base de datos, ¡pero en memoria es genial para aprender o testear!
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("1234")) // Encriptamos el texto "1234"
            .roles("ADMIN")      // Le damos permisos máximos de administrador
            .build();

        UserDetails user = User.builder()
            .username("pepe")
            .password(passwordEncoder().encode("pepe123"))
            .roles("USER")       // Solo puede comprar y ver partes de nivel usuario normal
            .build();

        // Guardar a "admin" y "pepe" momentáneamente en la memoria RAM del servidor.
        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * Motor de encriptación para las contraseñas.
     * Por buenas prácticas, jamás se guarda una contraseña en forma de texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}