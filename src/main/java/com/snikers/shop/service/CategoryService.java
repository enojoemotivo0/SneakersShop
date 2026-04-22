package com.snikers.shop.service; // El almacén trasero (Servicios) o taller oculto.

// Herramientas para manejar categorías o abrir los cajones (Repositorios) para sacarlas
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snikers.shop.model.Category;
import com.snikers.shop.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

/**
 * Este archivo funciona como el "Capataz del Almacén de Categorías".
 * Si un Recepcionista (Controlador) quiere una categoría (para enseñarla en la web), 
 * o si quiere guardar una, él no va a la base de datos (repositorio) y revuelve las cajas él mismo. ERROR.
 * El recepcionista le pide al Capataz: "Dile al mozo del almacén (CategoryRepository) que me traiga esto",
 * y este capataz se encarga de supervisarlo o hacer cálculos extra de seguridad si es necesario.
 */
@Service // Etiqueta que hace que Spring reconozca a esta parte del código como un "Capataz" (servicio de negocio).
@RequiredArgsConstructor // Magia lombok para conseguir rápido "llaves" del Almacen
@Transactional // Alerta universal de seguridad. "Si vas a hacer múltiples cambios a la vez y ALGO falla a mitad del proceso, devielvelo todo como estaba. ¡Nada de quedarse a medias!"
public class CategoryService { // Molde del capataz

    // La llave del almacén puro y duro (El Repositorio o mozo de caja). 
    private final CategoryRepository categoryRepository;

    // Cuando el recepcionista pide TODAS las cajas...
    // Le marcamos un (readOnly = true) que significa que solo vamos a leer. Eso hace que vaya más rápido.
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll(); // Ordenamos al mozo: "Tráeme todo y no mires lo que es."
    }

    // Cuando solo piden UNA pero la busca por DNI (ID)...
    @Transactional(readOnly = true)
    public Category findById(Long id) { // Recibe el ID
        // Mandamos buscar el ID... (pero en caso de que no encuentre nada le obligamos a quejarse (Throw))
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id)); // Excepción
    }

    // Oye, que el administrador web ha escrito una nueva categoría, vamos a GURDARLA.
    public Category save(Category category) {
        
        // Pero primero... el capataz revisa el texto, a ver si le falta la "dirección web" (slug).
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            
            // ¡Ostras! No la había puesto. Toma el nombre de la categoría, mételo en una centrifugadora y úsalo como dirección web.
            category.setSlug(toSlug(category.getName()));
        }
        
        // Todo listo, manda guardar.
        return categoryRepository.save(category);
    }

    // Si queremos ELIMINAR.
    public void delete(Long id) {
        categoryRepository.deleteById(id); // Elimina
    }

    // La famosa centrifugadora "toSlug" (Hace que "Zapatillas de Correr!!" se convierta en zapatillas-de-correr" limpio y listo para usarse como URL).
    private String toSlug(String name) {
        return name.toLowerCase() // Pasa todo a minúsculas
                .replaceAll("[^a-z0-9\\s-]", "") // Y quita todos los signos raros, mayúsculas, comas, exclamaciones...
                .replaceAll("\\s+", "-"); // Deja solo letra estándar unidas por guiones.
    }
}
