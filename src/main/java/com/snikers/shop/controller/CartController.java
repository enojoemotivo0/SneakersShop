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
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    @GetMapping
    public String view(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("pageTitle", "Tu carrito — SNIKERS");
        return "cart/view";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      @RequestParam(required = false) String size) {
        Product p = productService.findById(productId);
        cartService.add(p, quantity, size);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId,
                         @RequestParam(required = false) String size,
                         @RequestParam int quantity) {
        cartService.updateQuantity(productId, size, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId,
                         @RequestParam(required = false) String size) {
        cartService.remove(productId, size);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear() {
        cartService.clear();
        return "redirect:/cart";
    }
}
