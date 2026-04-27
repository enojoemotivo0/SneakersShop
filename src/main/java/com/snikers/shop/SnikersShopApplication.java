package com.snikers.shop; // Esta es la "carpeta" o "caja" principal donde guardamos el código de nuestra tienda.

// Estas líneas son como "importar herramientas" o "traer instrucciones" prefabricadas. 
// Nos ayudan a construir la página web mucho más rápido sin tener que inventar todo desde cero.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Snikers Shop.
 * (Aquí es donde arranca el motor de nuestra tienda de zapatillas).
 * 
 * Proyecto Transversal Final - 2º DAM / DAW
 * Arquitectura: Spring Boot MVC + Thymeleaf + MySQL + Docker
 */
@SpringBootApplication // Esta etiqueta es como un "interruptor mágico". Le dice al sistema que active todo lo necesario automáticamente para que nuestra tienda funcione en internet.
public class SnikersShopApplication { // Este es el "plano maestro" o el programa central de nuestra aplicación.

    // Este es el "botón de encendido" de todo el programa.
    // Cuando le damos a "iniciar" al programa, el ordenador busca esta línea exacta (main) para saber por dónde tiene que empezar a leer.
    public static void main(String[] args) {
        
        // Aquí es donde sucede la magia: le decimos a nuestro sistema "¡Arranca la tienda!".
        // Esto levanta el programa, lo prepara para recibir a los clientes y deja la tienda lista para funcionar.
        SpringApplication.run(SnikersShopApplication.class, args);
    }
}
