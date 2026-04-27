package com.snikers.shop.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
// Manejador global de excepciones para devolver una vista de error amigable.
public class GlobalExceptionHandler {

    // Errores de recurso/parametro invalido: se muestran como no encontrado.
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("pageTitle", "Recurso no encontrado");
        return "error";
    }

    // Errores de estado de negocio: operacion no permitida.
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, Model model) {
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("pageTitle", "Operación no válida");
        return "error";
    }

    // Fallback para cualquier error no controlado.
    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "Algo ha fallado en el servidor. Intenta de nuevo en unos minutos.");
        model.addAttribute("pageTitle", "Error interno");
        return "error";
    }
}
