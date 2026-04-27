package com.snikers.shop.config; // La carpeta virtual de configuraciones.

// Herramientas que nos permiten manejar números (precios) y listas de cosas.
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component; // Para encriptar las claves de prueba

import com.snikers.shop.model.Category;
import com.snikers.shop.model.Product;
import com.snikers.shop.model.User; // Molde de los usuarios
import com.snikers.shop.repository.CategoryRepository;
import com.snikers.shop.repository.ProductRepository;
import com.snikers.shop.repository.UserRepository; // Mozo de usuarios

import lombok.RequiredArgsConstructor;

/**
 * Este archivo es como el "Reponedor Automático" de la tienda.
 * Sirve para que, la primera vez que enciendas la página y la tienda esté vacía,
 * automáticamente coloque unas cuantas zapatillas de ejemplo en las estanterías (base de datos),
 * para que no se vea todo en blanco.
 */
@Component // Le decimos a la tienda: "Oye, este archivo es un empleado tuyo. Tenlo en cuenta."
@RequiredArgsConstructor // Le damos magia a nuestro empleado para conectar las piezas.
@SuppressWarnings("null")
public class DataInitializer implements CommandLineRunner { // "CommandLineRunner" significa que corre automáticamente al arrancar.

    // Nuestro reponedor necesita acceso a los armarios donde se guardan categorías y productos.
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // El armario de usuarios
    private final PasswordEncoder passwordEncoder; // La trituradora de contraseñas

    // Aquí están las instrucciones exactas de qué debe hacer el reponedor cuando entra a trabajar.
    @Override
    public void run(String... args) {
        // Garantiza SIEMPRE que los usuarios de prueba existan, independientemente
        // de si el catálogo ya fue cargado en ejecuciones anteriores.
        if (!userRepository.existsByEmail("cliente@snikers.shop")) {
            Objects.requireNonNull(userRepository.save(User.builder()
                    .fullName("Cliente de Prueba")
                    .email("cliente@snikers.shop")
                    .password(passwordEncoder.encode("cliente123"))
                    .role(User.Role.CLIENTE)
                    .build()));
        }
        if (!userRepository.existsByEmail("admin@snikers.shop")) {
            Objects.requireNonNull(userRepository.save(User.builder()
                    .fullName("Admin de Prueba")
                    .email("admin@snikers.shop")
                    .password(passwordEncoder.encode("admin1234"))
                    .role(User.Role.ADMINISTRADOR)
                    .build()));
        }

        // Si ya hay categorías, el catálogo está cargado. No reinsertar productos.
        if (categoryRepository.count() > 0) return;

        // ===== Se inventan las Categorías (los carteles de los pasillos) y se guardan =====
        Category running = Objects.requireNonNull(categoryRepository.save(Category.builder()
                .name("Running")
                .slug("running")
                .description("Sneakers ligeras con amortiguación máxima para correr largas distancias.")
                .build()));

        Category basketball = Objects.requireNonNull(categoryRepository.save(Category.builder()
                .name("Basketball")
                .slug("basketball")
                .description("Zapatillas de baloncesto con sujeción total y tracción agresiva.")
                .build()));

        Category lifestyle = Objects.requireNonNull(categoryRepository.save(Category.builder()
                .name("Lifestyle")
                .slug("lifestyle")
                .description("Sneakers icónicas pensadas para el día a día, con estilo urbano.")
                .build()));

        Category skate = Objects.requireNonNull(categoryRepository.save(Category.builder()
                .name("Skate")
                .slug("skate")
                .description("Modelos resistentes diseñados para skaters: suela vulcanizada y durabilidad.")
                .build()));

        // ===== Se inventa un montón de zapatillas de ejemplo, poniéndolas en sus categorías y asignando fotos ====
        productRepository.saveAll(Objects.requireNonNull(List.of(
                Product.builder()
                        .name("Air Prime Nova 91")
                        .brand("Aeroline") // Marca
                        .description("Amortiguación de aire visible, upper de malla ingeniería y suela de espuma energy-return. La elegida por corredores profesionales.")
                        .price(new BigDecimal("189.99")) // Precio normal
                        .originalPrice(new BigDecimal("229.99")) // Precio original antes de rebajas
                        .stock(42) // Cuántas tenemos en almacén
                        .color("Negro / Volt")
                        .sizeRange("38-47")
                        .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1200&q=80") // Enlace a la foto
                        .featured(true).active(true).category(running).build(), // destacado = si, activa = si, categoria = running

                // Repite el proceso para crear más zapatillas...
                Product.builder()
                        .name("Court Revolt Hyper")
                        .brand("Onyx")
                        .description("Zapatilla de basket con placa de carbono, cuello alto y tracción radial. Para dominar la cancha.")
                        .price(new BigDecimal("219.00"))
                        .stock(18)
                        .color("Rojo Fuego")
                        .sizeRange("39-48")
                        .imageUrl("https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=1200&q=80")
                        .featured(true).active(true).category(basketball).build(),

                Product.builder()
                        .name("Metro Classic Retro")
                        .brand("Urbano")
                        .description("Icono del streetwear reinventado. Piel premium, cordones planos y branding minimalista.")
                        .price(new BigDecimal("129.95"))
                        .stock(65)
                        .color("Blanco Roto")
                        .sizeRange("36-45")
                        .imageUrl("https://images.unsplash.com/photo-1549298916-b41d501d3772?w=1200&q=80")
                        .featured(true).active(true).category(lifestyle).build(),

                Product.builder()
                        .name("Deck Pro Vulcan")
                        .brand("Shred")
                        .description("Suela vulcanizada para máximo grip al tabla. Refuerzo doble en ollie area. Para skaters exigentes.")
                        .price(new BigDecimal("89.99"))
                        .originalPrice(new BigDecimal("109.99"))
                        .stock(120)
                        .color("Negro / Blanco")
                        .sizeRange("37-46")
                        .imageUrl("https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=1200&q=80")
                        .featured(true).active(true).category(skate).build(),

                Product.builder()
                        .name("Velocity Carbon X2")
                        .brand("Aeroline")
                        .description("La más rápida de la serie. Placa de fibra de carbono y espuma PEBA. Pensada para batir marcas.")
                        .price(new BigDecimal("259.00"))
                        .stock(8)
                        .color("Azul Eléctrico")
                        .sizeRange("40-46")
                        .imageUrl("https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=1200&q=80")
                        .featured(true).active(true).category(running).build(),

                Product.builder()
                        .name("High Top Legacy")
                        .brand("Onyx")
                        .description("Modelo retro de baloncesto de los 80 reinterpretado en piel suave y acolchado memory foam.")
                        .price(new BigDecimal("159.50"))
                        .stock(25)
                        .color("Negro Monocromo")
                        .sizeRange("39-46")
                        .imageUrl("https://images.unsplash.com/photo-1552346154-21d32810aba3?w=1200&q=80")
                        .featured(false).active(true).category(basketball).build(),

                Product.builder()
                        .name("Cloudwalker Lite")
                        .brand("Urbano")
                        .description("Hecha para caminar horas. Entresuela de espuma reactiva, peso pluma, upper de knit transpirable.")
                        .price(new BigDecimal("99.99"))
                        .stock(88)
                        .color("Gris Niebla")
                        .sizeRange("36-46")
                        .imageUrl("https://images.unsplash.com/photo-1539185441755-769473a23570?w=1200&q=80")
                        .featured(false).active(true).category(lifestyle).build(),

                Product.builder()
                        .name("Grind Master SL")
                        .brand("Shred")
                        .description("Silueta slim para skaters de precisión. Gamuza reforzada y suela cup ultra-flexible.")
                        .price(new BigDecimal("74.95"))
                        .stock(140)
                        .color("Verde Oliva")
                        .sizeRange("37-45")
                        .imageUrl("https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=1200&q=80")
                        .featured(false).active(true).category(skate).build()
        )));

        // Cuando termina, imprime este mensaje de satisfacción en la pantallita negra de herramientas (consola).
        System.out.println("✅ Datos iniciales cargados (categorías, productos y usuarios de prueba).");
    }
}
