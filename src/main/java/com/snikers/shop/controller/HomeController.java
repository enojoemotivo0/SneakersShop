package com.snikers.shop.controller; // Carpeta virtual "controladores". O lo que es lo mismo, el "Recepcionista".

// Importar los servicios (la trastienda donde se hace el trabajo duro).
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * Este archivo funciona como el recepcionista principal del centro comercial.
 * Cuando alguien entra por la puerta ("/" o "/home"), este archivo es quien da la bienvenida
 * y prepara las cajas (datos) de la página principal (Home).
 */
@Controller // Le decimos a Spring: "¡Trata a esta clase como un recepcionista que manda pantallas al usuario!".
@RequiredArgsConstructor // Magia para hacer más fácil coger las herramientas que necesitamos sin tanto teclear.
public class HomeController { // Empieza la plantilla del Recepcionista Principal.

    // Nuestro recepcionista necesita pedir ayuda a la trastienda para buscar zapatillas, categorías o saber cuántas cosas tienes en el carrito vacío.
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    // "ModelAttribute" significa: "Pon esta etiqueta en todas y cada una de las pantallas (plantillas visuales) de antemano".
    // Esta en concreto calcula y pega un numerito que dice "carrito (X)" en la esquinita de la pantalla, que te sigue a todas partes.
    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getTotalItems(); // Contamos artículos "al vuelo".
    }

    // Igual que el anterior, pero llenamos de forma universal una lista con todas las "Categorías" de la tienda,
    // por si necesitamos un "menú desplegable" en cualquier pantalla.
    @ModelAttribute("allCategories")
    public Object allCategories() {
        return categoryService.findAll(); // Coger toda lista sin excepciones.
    }

    // Aquí le decimos: "Cuando un cliente escriba 'misitio.com/' o 'misitio.com/home' en su navegador de Internet..."
    @GetMapping({"/", "/home"})
    public String home(Model model) { // La caja de cartón (Model) que le pasamos a la pantalla de HTML...
        
        // Empaquetamos en la caja solo las zapatillas que tienen una "Estrella amarilla" (productos destacados).
        model.addAttribute("featuredProducts", productService.findFeatured());
        
        // Empaquetamos en la caja todas las categorías principales.
        model.addAttribute("categories", categoryService.findAll());
        
        // Empaquetamos unas cuantas etiquetas con títulos potentes y explicaciones para que luego queden bonitas en las pestañas y buscadores como el de Google.
        model.addAttribute("pageTitle", "SNIKERS — Sneakers premium que marcan la diferencia");
        model.addAttribute("pageDescription",
                "Descubre las sneakers más exclusivas del mercado. Running, basketball, lifestyle y skate. Envío gratis desde 50€.");
        
        // Por último, gritamos a los de las plantillas visuales: "¡Venga, montadme la pantalla 'home' con estas cajas que os he empaquetado!"
        return "home"; // Devolvemos el chasis de la pantalla inicial.
    }
}
