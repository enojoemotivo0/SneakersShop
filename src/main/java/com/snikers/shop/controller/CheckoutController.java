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
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final CategoryService categoryService;

    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

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

    @PostMapping
    public String processCheckout(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam String shippingAddress,
                                  @RequestParam(required = false) String fullName,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam(required = false) String email,
                                  Model model) {
        if (cartService.isEmpty()) return "redirect:/cart";
        
        User user = userService.findByEmail(userDetails.getUsername());
        
        // Actualizar datos del usuario si los ha cambiado en el formulario
        boolean updated = false;
        if (fullName != null && !fullName.isBlank() && !fullName.equals(user.getFullName())) {
            user.setFullName(fullName);
            updated = true;
        }
        if (phone != null && !phone.equals(user.getPhone())) {
            user.setPhone(phone);
            updated = true;
        }
        boolean emailUpdated = false;
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
        
        Order order = orderService.createFromCart(user, cartService.getItems(), shippingAddress);
        cartService.clear();
        return "redirect:/orders/" + order.getId() + "?confirmed=true";
    }
}
