package com.snikers.shop.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.snikers.shop.model.User;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("null")
// Gestiona autenticación y registro de usuarios.
public class AuthController {

    private final UserService userService;
    private final CartService cartService;
    private final CategoryService categoryService;

    // Disponible en todas las vistas: cantidad total de items del carrito.
    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    // Disponible en todas las vistas: lista global de categorías para menús/filtros.
    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    // Muestra el formulario de login y mensajes de estado (error/logout).
    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        model.addAttribute("pageTitle", "Iniciar sesión — SNIKERS");
        if (error != null) model.addAttribute("errorMessage", "Email o contraseña incorrectos");
        if (logout != null) model.addAttribute("successMessage", "Has cerrado sesión correctamente");
        return "auth/login";
    }

    // Inicializa el formulario de registro con un User vacío si aún no existe en el modelo.
    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
        return "auth/register";
    }

    // Procesa el alta de usuario, valida datos y opcionalmente convierte la foto a Base64.
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
                // Se guarda la imagen como Data URI para poder renderizarla directamente en la vista.
                String base64Image = Base64.getEncoder().encodeToString(photoFile.getBytes());
                user.setProfilePicture("data:" + photoFile.getContentType() + ";base64," + base64Image);
            }
            userService.register(user);
        } catch (IllegalArgumentException ex) {
            // Error de negocio típico: email ya existente.
            String errorMessage = Objects.requireNonNullElse(ex.getMessage(), "Ya existe un usuario con ese email");
            bindingResult.rejectValue("email", "email.duplicate", errorMessage);
            model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
            return "auth/register";
        } catch (IOException ex) {
            // Error técnico al leer/subir la imagen.
            model.addAttribute("errorMessage", "Error al procesar la foto de perfil.");
            model.addAttribute("pageTitle", "Crear cuenta — SNIKERS");
            return "auth/register";
        }
        return "redirect:/login?registered=true";
    }
}
