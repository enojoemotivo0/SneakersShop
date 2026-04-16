package com.tienda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tienda.service.ProductoService;

/**
 * HomeController maneja las peticiones a la ruta raíz y la página de login.
 * Se encarga de conectar al usuario que entra por la web con la vista (Thymeleaf).
 */
@Controller
public class HomeController {

    // @Autowired inyecta automáticamente la instancia (dependency injection).
    // Aquí invocamos lógica de negocio de los productos.
    @Autowired
    private ProductoService productoService;

    /**
     * Mapea la petición GET a la raíz del sitio ("/").
     * El objeto Model permite pasar datos desde Java a la plantilla HTML.
     */
    @GetMapping("/")
    public String home(Model model) {
        // Obtenemos todos los productos desde la base de datos (con el servicio) y los enviamos a la vista
        model.addAttribute("productos", productoService.findAll());
        // Devuelve el nombre "home", que Spring buscará como "home.html" en src/main/resources/templates/
        return "home";
    }

    /**
     * Mapea la petición para abrir la pantalla de inicio de sesión ("/login")
     */
    @GetMapping("/login")
    public String login() {
        // Devuelve el nombre de la plantilla "login.html"
        return "login";
    }
}
