package com.snikers.shop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.snikers.shop.model.Order;
import com.snikers.shop.model.User;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.OrderService;
import com.snikers.shop.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
// Gestiona el flujo de finalizacion de compra.
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final CategoryService categoryService;

    // Disponible en todas las vistas: contador de items en carrito.
    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    // Disponible en todas las vistas: categorias para menu/filtros.
    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    // Muestra el formulario de checkout con datos actuales del usuario autenticado.
    @GetMapping
    public String checkoutForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (cartService.isEmpty()) return "redirect:/cart";
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Finalizar compra — SNIKERS");
        return "cart/checkout";
    }

    // Confirma la compra, actualiza datos del usuario y genera el pedido desde el carrito.
    @PostMapping
    public String processCheckout(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam String shippingAddress,
                                  @RequestParam(required = false) String fullName,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(defaultValue = "EFECTIVO") String paymentMethod,
                                  Model model) {
        if (cartService.isEmpty()) return "redirect:/cart";
        
        User user = userService.findByEmail(userDetails.getUsername());
        
        // Validar que el nuevo email no esté en uso por otro usuario antes de aplicar cambios.
        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            if (userService.existsByEmail(email)) {
                // Email ya registrado: devolver el formulario con mensaje de error.
                model.addAttribute("emailError", "Este email ya está en uso por otro usuario");
                model.addAttribute("items", cartService.getItems());
                model.addAttribute("total", cartService.getTotal());
                model.addAttribute("user", user);
                model.addAttribute("pageTitle", "Finalizar compra — SNIKERS");
                return "cart/checkout";
            }
        }
        
        // Actualiza datos del perfil solo si hubo cambios en el formulario.
        boolean updated = false;
        boolean emailUpdated = false;
        if (fullName != null && !fullName.isBlank() && !fullName.equals(user.getFullName())) {
            user.setFullName(fullName);
            updated = true;
        }
        if (phone != null && !phone.equals(user.getPhone())) {
            user.setPhone(phone);
            updated = true;
        }
        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            user.setEmail(email);
            updated = true;
            emailUpdated = true;
        }
        if (shippingAddress != null && !shippingAddress.isBlank() && !shippingAddress.equals(user.getAddress())) {
            user.setAddress(shippingAddress);
            updated = true;
        }
        
        if (updated) {
            userService.update(user);
            // Si cambia el email, actualizar el principal de seguridad con el nuevo username.
            if (emailUpdated) {
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                userService.loadUserByUsername(email),
                                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        )
                );
            }
        }

        // Parsear método de pago; usar EFECTIVO como fallback si el valor no es válido.
        Order.PaymentMethod pm;
        try {
            pm = Order.PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            pm = Order.PaymentMethod.EFECTIVO;
        }
        
        // Crea el pedido, vacia carrito y redirige a detalle con flag de confirmacion.
        Order order = orderService.createFromCart(user, cartService.getItems(), shippingAddress, pm);
        cartService.clear();
        return "redirect:/orders/" + order.getId() + "?confirmed=true";
    }
}
