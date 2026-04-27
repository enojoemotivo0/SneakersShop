package com.snikers.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Snikers Shop.
 * Arquitectura: Spring Boot MVC + Thymeleaf + MySQL + Spring Security
 * 
 * Proyecto Transversal Final - 2º DAM / DAW
 */
@SpringBootApplication
public class SnikersShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnikersShopApplication.class, args);
    }
}
