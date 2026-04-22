package com.snikers.shop.model; // Carpeta virtual donde definimos "cómo son las cosas" en la tienda (modelos).

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Este archivo define qué es una "Categoría" en nuestra tienda (Ej: Running, Baloncesto).
 * Imagínate que es como crear la ficha o el molde con el que fabricaremos todas las categorías.
 */
@Entity // Le dice al programa: "Crea una tabla en la base de datos para guardar esto"
@Table(name = "categorias") // El nombre del cajón o "tabla" en la base de datos será "categorias"
@Getter @Setter // Magia para "leer" y "escribir" información en esta ficha sin teclear tanto código.
@NoArgsConstructor // Magia para poder crear una categoría en blanco.
@AllArgsConstructor // Magia para crear una categoría pasando todos los datos de golpe.
@Builder // Magia para construir categorías paso a paso (nombre="Running", descripcion="Para correr").
public class Category { // El molde de la Categoría.

    @Id // Este dato es el "Número de DNI" o identificador único de la categoría. No puede haber dos iguales.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le decimos: "Dale tú solo el siguiente número automáticamente (1, 2, 3...)".
    private Long id; // Aquí guardamos su número DNI interno.

    @NotBlank(message = "El nombre de la categoría es obligatorio") // Regla: no pueden dejar el nombre vacío o en blanco.
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres") // Regla de tamaño máximo/mínimo.
    @Column(name = "nombre", nullable = false, unique = true, length = 50) // En la base de datos, este texto es obligatorio (no nulo) y único.
    private String name; // Aquí guardamos el nombre real de la categoría.

    @Size(max = 255) // Solo permitimos descripciones breves (255 letras máximo).
    @Column(name = "descripcion", length = 255) // Avisamos a la base de datos del tamaño máximo.
    private String description; // Aquí guardamos la descripción ("Ideal para correr largas distancias...").

    @Column(name = "url_amigable", length = 100) 
    private String slug; // Aquí guardamos el texto que se pone arriba en el navegador (ejemplo: "super-zapatillas-running")

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
