package com.tienda.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tienda.model.Producto;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;

/**
 * 🎓 CONTROLADOR: ProductoController
 * Los controladores actúan como "recepcionistas" en nuestra aplicación.
 * Reciben las peticiones del usuario desde el navegador, deciden qué hacer comunicándose
 * con los Servicios y, finalmente, devuelven la vista (página HTML) adecuada al usuario.
 */
@Controller // Indica a Spring que esta clase manejará peticiones web y devolverá vistas (plantillas Thymeleaf).
@RequestMapping("/productos") // Todas las URL de este controlador empezarán con "/productos" (ej: localhost:8080/productos/nuevo).
public class ProductoController {

    @Autowired // Inyección de dependencias: Spring automáticamente nos proporciona una instancia lista de ProductoService.
    private ProductoService productoService;

    @Autowired // Spring nos da acceso al servicio de Categorías para usarlo aquí.
    private CategoriaService categoriaService;

    /**
     * @GetMapping(""): Atiende peticiones HTTP tipo GET a la ruta base ("/productos").
     * Se encarga de mostrar la lista de productos, incluyendo paginación y la función de búsqueda.
     */
    @GetMapping("")
    public String list(@RequestParam(defaultValue = "0") int page, 
                       @RequestParam(required = false) String buscar, 
                       Model model) {
        // ... (código que maneja la vista de la lista)
        Pageable pageable = PageRequest.of(page, 5); // 5 productos por página
        Page<Producto> productosPage;
        
        if (buscar != null && !buscar.isEmpty()) {
            productosPage = productoService.findByNombre(buscar, pageable);
            model.addAttribute("buscar", buscar);
        } else {
            productosPage = productoService.findAll(pageable);
        }
        
        // El 'Model' es una mochila donde guardamos datos que le pasaremos al archivo HTML para que se pinte en pantalla.
        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productosPage.getTotalPages());
        
        return "producto/lista"; // Retorna la vista en src/main/resources/templates/producto/lista.html
    }

    /**
     * @GetMapping("/nuevo"): Muestra el formulario vacío para crear un nuevo producto.
     */
    @GetMapping("/nuevo")
    public String createForm(Model model) {
        model.addAttribute("producto", new Producto()); // Objeto vacío que rellenará el usuario en el formulario.
        model.addAttribute("categorias", categoriaService.findAll()); // Lista de categorías para el menú desplegable.
        return "producto/form";
    }

    /**
     * @PostMapping("/guardar"): Recibe los datos del formulario cuando el usuario hace clic en "Guardar".
     * @Valid permite que Spring valide los datos (las anotaciones @NotBlank, etc., del Modelo).
     * @ModelAttribute capta la información que vino agrupada desde el HTML.
     */
    @PostMapping("/guardar")
    public String save(@jakarta.validation.Valid @ModelAttribute Producto producto, org.springframework.validation.BindingResult result, @RequestParam(value = "file", required = false) MultipartFile file, Model model) {
        // Si hay errores de validación (por ejemplo, dejaron vacío un campo obligatorio), devolvemos el formulario con errores.
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAll());
            return "producto/form";
        }
        
        // Lógica para guardar la imagen
        if (file != null && !file.isEmpty()) {
            try {
                // Crear carpeta uploads en la raíz del proyecto si no existe
                Path dirPaths = Paths.get("uploads");
                if (!Files.exists(dirPaths)) {
                    // ... (código guardando imagen)
                    Files.createDirectories(dirPaths);
                }

                // Generar nombre automático y único (UUID) para evitar reemplazar archivos con mismo nombre
                String originalFilename = file.getOriginalFilename();
                String extension = (originalFilename != null && originalFilename.contains(".")) ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                String newFileName = UUID.randomUUID().toString() + extension;
                
                // Guardar la foto físicamente
                Path path = Paths.get("uploads/" + newFileName);
                Files.write(path, file.getBytes());

                // Guardar la etiqueta o referencia en nuestro objeto del producto
                producto.setImagenUrl("/images/" + newFileName);
            } catch (IOException e) {
                System.err.println("Error al guardar la imagen: " + e.getMessage());
            }
        }
        
        // Mandamos el producto ya procesado a que se guarde en la BD.
        productoService.save(producto);
        
        // "redirect:" le dice al navegador que vaya a otra URL como si el usuario lo hubiera escrito (hace un refresh)
        return "redirect:/productos";
    }

    /**
     * @GetMapping("/editar/{id}"): Busca el producto por ID y lo pone en el formulario para editarlo.
     */
    @GetMapping("/editar/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.findById(id).orElse(null));
        model.addAttribute("categorias", categoriaService.findAll());
        return "producto/form";
    }

    /**
     * @GetMapping("/eliminar/{id}"): Elimina un producto pasándole su ID en la URL.
     */
    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable Long id) {
        productoService.deleteById(id);
        return "redirect:/productos";
    }
}
