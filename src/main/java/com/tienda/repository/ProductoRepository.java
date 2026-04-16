package com.tienda.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.model.Producto;

/**
 * 🎓 REPOSITORIO: ProductoRepository
 * Esta interfaz es la encargada de comunicarse DIRECTAMENTE con la base de datos.
 * Gracias a Spring Data JPA, no necesitamos escribir consultas SQL largas a mano (¡magia!).
 */
@Repository // Le dice a Spring que esta clase es un componente dedicado al acceso y manejo de datos en la BD.
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    /**
     * JpaRepository ya nos regala métodos básicos como: findAll(), save(), deleteById(), findById().
     * Pero aquí estamos creando un método personalizado con solo nombrar bien la función.
     * 
     * Al llamarlo "findByNombreContainingIgnoreCase", Spring Data infiere que queremos:
     * - Buscar por el campo "Nombre" del Producto.
     * - "Containing": Que contenga la palabra (como un LIKE %palabra% en SQL).
     * - "IgnoreCase": Que ignore si está en mayúsculas o minúsculas.
     * - Pageable: Permite que los resultados vengan paginados (listas divididas en páginas).
     */
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
