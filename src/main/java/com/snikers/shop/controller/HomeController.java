package com.snikers.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador principal de la home pública.
 * Prepara datos de portada y metadatos SEO básicos.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    // Servicios de negocio usados para poblar la pantalla inicial.
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    // Disponible en todas las vistas: contador total de items en carrito.
    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getTotalItems();
    }

    // Disponible en todas las vistas: categorías para navegación y filtros.
    @ModelAttribute("allCategories")
    public Object allCategories() {
        return categoryService.findAll();
    }

    // Renderiza la página principal con productos destacados y categorías.
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("featuredProducts", productService.findFeatured());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "SNIKERS — Sneakers premium que marcan la diferencia");
        model.addAttribute("pageDescription",
                "Descubre las sneakers más exclusivas del mercado. Running, basketball, lifestyle y skate. Envío gratis desde 50€.");
        return "home";
    }
}
