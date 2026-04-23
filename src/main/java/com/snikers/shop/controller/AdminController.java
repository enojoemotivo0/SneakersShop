package com.snikers.shop.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.snikers.shop.model.Category;
import com.snikers.shop.model.Order;
import com.snikers.shop.model.Product;
import com.snikers.shop.model.User;
import com.snikers.shop.repository.UserRepository;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.OrderService;
import com.snikers.shop.service.ProductService;
import com.snikers.shop.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final UserRepository userRepository;

    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    // ---------- Dashboard ----------
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.count());
        model.addAttribute("totalOrders", orderService.findAll().size());
        model.addAttribute("totalCategories", categoryService.findAll().size());
        model.addAttribute("recentOrders", orderService.findAll().stream().limit(5).toList());
        model.addAttribute("pageTitle", "Admin — Panel SNIKERS");
        return "admin/dashboard";
    }

    // ---------- Productos ----------
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.findAllActive());
        model.addAttribute("pageTitle", "Admin — Productos");
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Nuevo producto");
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Editar producto");
        return "admin/product-form";
    }

    @PostMapping("/products")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult,
                              @RequestParam Long categoryId,
                              @RequestParam(value = "photoFile", required = false) org.springframework.web.multipart.MultipartFile photoFile,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("pageTitle", "Nuevo producto");
            return "admin/product-form";
        }
        
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                String base64Image = java.util.Base64.getEncoder().encodeToString(photoFile.getBytes());
                product.setImageUrl("data:" + photoFile.getContentType() + ";base64," + base64Image);
            } catch (java.io.IOException e) {
                // Ignore silent update error when trying to parse image bytes
                System.err.println("Error procesando imagen: " + e.getMessage());
            }
        }
        
        product.setCategory(categoryService.findById(categoryId));
        if (product.getId() == null) {
            productService.save(product);
        } else {
            productService.update(product.getId(), product);
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.softDelete(id);
        return "redirect:/admin/products";
    }

    // ---------- Categorías ----------
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        if (!model.containsAttribute("category")) model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Admin — Categorías");
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/categories";
        }
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    // ---------- Pedidos ----------
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("pageTitle", "Admin — Pedidos");
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, Order.Status.valueOf(status));
        return "redirect:/admin/orders";
    }

    // ---------- Usuarios / Clientes ----------
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        if (!model.containsAttribute("user")) model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Admin — Clientes / Usuarios");
        return "admin/users";
    }

    @PostMapping("/users")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "admin/users";
        }
        try {
            if (photoFile != null && !photoFile.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(photoFile.getBytes());
                user.setProfilePicture("data:" + photoFile.getContentType() + ";base64," + base64Image);
            }
            userService.register(user);
        } catch(IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.user", e.getMessage());
            model.addAttribute("users", userRepository.findAll());
            return "admin/users";
        } catch(IOException e) {
            model.addAttribute("errorMessage", "Error al procesar la foto de perfil.");
            model.addAttribute("users", userRepository.findAll());
            return "admin/users";
        }
        return "redirect:/admin/users";
    }
}
