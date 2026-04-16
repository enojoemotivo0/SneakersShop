package com.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (Entry Point) de nuestra aplicación Spring Boot.
 * La anotación @SpringBootApplication le dice a Spring que autoconfigure
 * la aplicación, escanee los componentes (controladores, servicios, etc.)
 * en este paquete y subpaquetes, y active la configuración basada en clases.
 */
@SpringBootApplication
public class TiendaApplication {

	/**
	 * Método main: arranca el servidor web integrado (por defecto Tomcat)
	 * y despacha la ejecución al framework de Spring.
	 */
	public static void main(String[] args) {
		SpringApplication.run(TiendaApplication.class, args);
	}

}
