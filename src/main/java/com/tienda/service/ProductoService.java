package com.tienda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tienda.model.Producto;

/**
 * 🎓 INTERFAZ DE SERVICIO: ProductoService
 * Aquí definimos los "contratos" o las "reglas" de lo que se puede hacer con los productos.
 * La capa de Servicio es donde va la Lógica de Negocio. Su función es servir de puente 
 * entre el Controlador (la web) y el Repositorio (la base de datos), añadiendo cualquier 
 * regla o comprobación antes de guardar o buscar.
 */
public interface ProductoService {
    
    // Devuelve todos los productos que existen en la BD.
    List<Producto> findAll();
    
    // Devuelve los productos de forma paginada (útil para no saturar si hay miles).
    Page<Producto> findAll(Pageable pageable);
    
    // Busca productos que contengan un texto en su nombre de forma paginada.
    Page<Producto> findByNombre(String nombre, Pageable pageable);
    
    // Busca un producto por su ID. Devuelve 'Optional' por si acaso el producto no se encuentra.
    Optional<Producto> findById(Long id);
    
    // Guarda el producto en la base de datos (sirve tanto para crear uno nuevo como para actualizarlo).
    Producto save(Producto producto);
    
    // Borra un producto de la base de datos usando su ID.
    void deleteById(Long id);
}
