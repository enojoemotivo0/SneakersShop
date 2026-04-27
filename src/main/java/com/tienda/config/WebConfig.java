package com.tienda.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Configuramos Spring para que cuando el navegador pida algo de /images/
        // lo busque en la carpeta "uploads" que está en la raíz de tu proyecto.
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
    }
}