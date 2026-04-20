package com.snikers.shop.controller;

import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;
import com.snikers.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getTotalItems();
    }

    @ModelAttribute("allCategories")
    public Object allCategories() {
        return categoryService.findAll();
    }

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
