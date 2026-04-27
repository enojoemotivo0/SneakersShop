package com.snikers.shop.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.snikers.shop.model.Order;
import com.snikers.shop.model.User;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.OrderService;
import com.snikers.shop.service.PdfService;
import com.snikers.shop.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
// Gestiona historial de pedidos, detalle y descarga de factura.
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final CategoryService categoryService;
    private final PdfService pdfService;

    // Disponible en todas las vistas: contador de items en carrito.
    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    // Disponible en todas las vistas: categorias para navegacion.
    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    // Lista los pedidos del usuario autenticado.
    @GetMapping
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("orders", orderService.findByUser(user.getId()));
        model.addAttribute("pageTitle", "Mis pedidos — SNIKERS");
        return "user/orders";
    }

    // Muestra el detalle de un pedido si el usuario tiene permisos.
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(required = false) Boolean confirmed,
                         @AuthenticationPrincipal UserDetails userDetails,
                         Model model) {
        Order order = orderService.findById(id);
        User user = userService.findByEmail(userDetails.getUsername());
        // Solo el dueño o un admin pueden ver un pedido
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMINISTRADOR) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        model.addAttribute("confirmed", Boolean.TRUE.equals(confirmed));
        model.addAttribute("pageTitle", "Pedido " + order.getOrderNumber() + " — SNIKERS");
        return "user/order-detail";
    }

    // Genera y devuelve la factura PDF del pedido cuando el usuario esta autorizado.
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.findById(id);
        User user = userService.findByEmail(userDetails.getUsername());
        
        // Solo el dueno o un admin pueden descargar la factura.
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMINISTRADOR) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdfBytes = pdfService.generateInvoice(order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Factura_" + order.getOrderNumber() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }}
