package com.snikers.shop.controller;

import com.snikers.shop.model.Product;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
// Gestiona operaciones del carrito de compra.
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final CategoryService categoryService;

    // Disponible en todas las vistas: contador de items en carrito.
    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    // Disponible en todas las vistas: categorias para menu/filtros.
    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    // Muestra el resumen actual del carrito.
    @GetMapping
    public String view(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("pageTitle", "Tu carrito — SNIKERS");
        return "cart/view";
    }

    // Agrega un producto al carrito con cantidad y talla opcional.
    @PostMapping("/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      @RequestParam(required = false) String size) {
        Product p = productService.findById(productId);
        cartService.add(p, quantity, size);
        return "redirect:/cart";
    }

    // Actualiza la cantidad de una linea del carrito.
    @PostMapping("/update")
    public String update(@RequestParam Long productId,
                         @RequestParam(required = false) String size,
                         @RequestParam int quantity) {
        cartService.updateQuantity(productId, size, quantity);
        return "redirect:/cart";
    }

    // Elimina una linea especifica del carrito.
    @PostMapping("/remove")
    public String remove(@RequestParam Long productId,
                         @RequestParam(required = false) String size) {
        cartService.remove(productId, size);
        return "redirect:/cart";
    }

    // Vacia por completo el carrito actual.
    @PostMapping("/clear")
    public String clear() {
        cartService.clear();
        return "redirect:/cart";
    }
}
