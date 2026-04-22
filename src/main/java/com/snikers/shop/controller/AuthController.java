package com.snikers.shop.controller;

import com.snikers.shop.model.User;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CartService cartService;
    private final CategoryService categoryService;

    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        model.addAttribute("pageTitle", "Iniciar sesión — SNIKERS");
        if (error != null) model.addAttribute("errorMessage", "Email o contraseña incorrectos");
        if (logout != null) model.addAttribute("successMessage", "Has cerrado sesión correctamente");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
            return "auth/register";
        }
        try {
            if (photoFile != null && !photoFile.isEmpty()) {
                String base64Image = Base64.getEncoder().encodeToString(photoFile.getBytes());
                user.setProfilePicture("data:" + photoFile.getContentType() + ";base64," + base64Image);
            }
            userService.register(user);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("email", "email.duplicate", ex.getMessage());
            model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
            return "auth/register";
        } catch (IOException ex) {
            model.addAttribute("errorMessage", "Error al procesar la foto de perfil.");
            model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
            return "auth/register";
        }
        return "redirect:/login?registered=true";
    }
}
