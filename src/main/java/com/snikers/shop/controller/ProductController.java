package com.snikers.shop.controller;

import com.snikers.shop.model.Product;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
// Gestiona el catálogo público, filtros, detalle y búsqueda.
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    // Disponible en todas las vistas: contador total de items en carrito.
    @ModelAttribute("cartCount")
    public int cartCount() { return cartService.getTotalItems(); }

    // Disponible en todas las vistas: categorías para navegación y filtros.
    @ModelAttribute("allCategories")
    public Object allCategories() { return categoryService.findAll(); }

    // Lista productos con paginación, ordenamiento y filtros opcionales por categoría o búsqueda.
    @GetMapping("/products")
    public String list(@RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       @RequestParam(defaultValue = "createdAt,desc") String sort,
                       Model model) {

        // Formato esperado en sort: campo,direccion (ej: createdAt,desc).
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        Page<Product> products;
        if (q != null && !q.isBlank()) {
            // Prioridad 1: búsqueda textual.
            products = productService.search(q, pageable);
            model.addAttribute("query", q);
            model.addAttribute("pageTitle", "Resultados: " + q + " — SNIKERS");
        } else if (categoryId != null) {
            // Prioridad 2: filtro por categoría.
            products = productService.findByCategory(categoryId, pageable);
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("pageTitle", "Catálogo — SNIKERS");
        } else {
            // Caso base: catálogo general con productos activos.
            products = productService.findAllActive(pageable);
            model.addAttribute("pageTitle", "Catálogo — SNIKERS");
        }

        model.addAttribute("products", products);
        model.addAttribute("currentSort", sort);
        model.addAttribute("pageDescription",
                "Explora todas las sneakers premium de la colección Snikers. Filtra por marca, categoría y precio.");
        return "products/list";
    }

    // Muestra detalle de un producto por id.
    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", product.getName() + " — SNIKERS");
        model.addAttribute("pageDescription",
                // Limita la descripción para metadatos (SEO/snippet) y evita textos excesivos.
                product.getBrand() + " " + product.getName() + ". " +
                (product.getDescription() != null ? product.getDescription().substring(0, Math.min(140, product.getDescription().length())) : ""));
        return "products/detail";
    }

    // Endpoint de conveniencia: redirige la búsqueda a la lógica estándar de listado.
    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        return list(null, q, 0, 12, "createdAt,desc", model);
    }
}
