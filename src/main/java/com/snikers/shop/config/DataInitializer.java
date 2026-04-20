package com.snikers.shop.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.snikers.shop.model.Category;
import com.snikers.shop.model.Product;
import com.snikers.shop.repository.CategoryRepository;
import com.snikers.shop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

/**
 * Inicializa la BD con datos de demo al arrancar la aplicación.
 * Solo se ejecuta si no hay datos.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        // ===== Categorías =====
        Category running = categoryRepository.save(Category.builder()
                .name("Running")
                .slug("running")
                .description("Sneakers ligeras con amortiguación máxima para correr largas distancias.")
                .build());

        Category basketball = categoryRepository.save(Category.builder()
                .name("Basketball")
                .slug("basketball")
                .description("Zapatillas de baloncesto con sujeción total y tracción agresiva.")
                .build());

        Category lifestyle = categoryRepository.save(Category.builder()
                .name("Lifestyle")
                .slug("lifestyle")
                .description("Sneakers icónicas pensadas para el día a día, con estilo urbano.")
                .build());

        Category skate = categoryRepository.save(Category.builder()
                .name("Skate")
                .slug("skate")
                .description("Modelos resistentes diseñados para skaters: suela vulcanizada y durabilidad.")
                .build());

        // ===== Productos =====
        productRepository.saveAll(List.of(
                Product.builder()
                        .name("Air Prime Nova 91")
                        .brand("Aeroline")
                        .description("Amortiguación de aire visible, upper de malla ingeniería y suela de espuma energy-return. La elegida por corredores profesionales.")
                        .price(new BigDecimal("189.99"))
                        .originalPrice(new BigDecimal("229.99"))
                        .stock(42)
                        .color("Negro / Volt")
                        .sizeRange("38-47")
                        .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1200&q=80")
                        .featured(true).active(true).category(running).build(),

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
        ));

        System.out.println("✅ Datos iniciales cargados (solo categorías y productos).");
    }
}
