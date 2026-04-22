package com.snikers.shop.repository; // La carpeta de repositorios o "Los Mozo de Almacén".

// Herramientas que necesitan buscar Categorías.
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snikers.shop.model.Category;

/**
 * Este archivo funciona como el "Mozo de almacén de Categorías".
 * Él es el único que baja al sótano (la base de datos), abre la Excel (la tabla de Categorías) y nos trae la info cruda.
 * No pregunta, no procesa la info; solo lee, guarda y borra como un robot, porque el Capataz (Service) se lo manda.
 */
@Repository // Avisa a Spring: "Oye, este tipo te hace el trabajo sucio en la DB".
// Extiende (Hereda) superpoderes mágicos ("JpaRepository") de Spring.
// Se le dice: Tu molde a manejar es "Category" (La Ficha) y lo buscarás por su DNI o ID, que tiene formato "Long" (Número muy largo).
// Esto auto-fabrica un millón de comandos invisibles en segundo plano: Guardar(), Borrar(), BorrarTodos(), EncontrarTodos(). NO TENEMOS NI QUE ESCRIBIRLOS.
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Pero si queremos búsquedas "Especiales"...
    
    // Le decimos a Spring: "Créame automáticamente, sin que yo escriba cómo..."
    // "...un comando para buscar categorías introduciendo un texto (name) que NO se fije si tiene mayúsculas (IgnoreCase)."
    // Es "Optional" porque podría no encontrar lo que pedimos (como "buscar zapas que no existen").
    Optional<Category> findByNameIgnoreCase(String name);

    // "...un comando para buscar categorías usando solo la URL del navegador que le hemos puesto (slug)".
    Optional<Category> findBySlug(String slug);

    // "...y devuélveme un simple VERDADERO o FALSO comprobando directamente si una categoría existe (existsBy) buscándola por texto (Name)."
    boolean existsByNameIgnoreCase(String name);
}
