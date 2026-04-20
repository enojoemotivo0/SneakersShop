package com.snikers.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Snikers Shop.
 * 
 * Proyecto Transversal Final - 2º DAM / DAW
 * Arquitectura: Spring Boot MVC + Thymeleaf + MySQL + Docker
 */
@SpringBootApplication
public class SnikersShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnikersShopApplication.class, args);
    }
}
