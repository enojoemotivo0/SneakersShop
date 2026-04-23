package com.snikers.shop.model; // Carpeta virtual "modelos" (donde guardamos las fichas o plantillas de las cosas reales).

// Herramientas para números raros (precios) y fechas/horas (cuándo se subió a la web o se modificó).
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Este archivo define qué es un "Producto" (las zapatillas).
 * Piensa en esto como la etiqueta del precio y especificaciones que cuelga del producto físico.
 */
@Entity // Otra vez: "¡Créame una hoja o tabla en nuestra tienda digital para esto!"
@Table(name = "productos", indexes = { // "Llama a la carpeta 'productos' y ordena alfabéticamente por 'nombre' y 'marca' para buscar rápido."
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_brand", columnList = "brand")
})
@Getter @Setter // Magia para autogenerar el poder de cambiar o leer la talla, nombre, etc. sin escribir tanto.
@NoArgsConstructor // Molde en blanco
@AllArgsConstructor // Molde rellenado
@Builder // Para crearlo paso a paso 
public class Product {

    @Id // El 'código de barras'. Único e irrepetible.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // "Sumálos 1, 2, 3..." para que no nos liemos buscando qué número estaba libre.
    private Long id; // El recipiente de ese número.

    @NotBlank(message = "El nombre es obligatorio") // Freno de mano: ¡No puedes crear zapatilla sin nombre!
    @Size(min = 2, max = 120) // Ni muy corto ni súper largo.
    @Column(name = "nombre", nullable = false, length = 120) // Lo reforzamos también en la propia base de datos (sin él da error al guardar).
    private String name; // Contenedor del nombre real.

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 60)
    @Column(name = "marca", nullable = false, length = 60) // Refuerzos.
    private String brand; // La marca (Ej: Onyx, Shred).

    @Size(max = 2000)
    @Column(name = "descripcion", length = 2000) // Mucho más margen aquí, como 2000 letras ("texto largo")
    private String description; // Donde va la charleta o cuento comercial describiendo cómo es la zapatilla.

    @NotNull(message = "El precio es obligatorio") // ¿Una zapatilla sin precio? ¡Ni hablar!
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0") // ¿Gratis? No aquí.
    @Digits(integer = 7, fraction = 2) // Un montón de números posibles y máximo dos céntimos (dos decimales).
    @Column(name = "precio", nullable = false, precision = 9, scale = 2) // Lo aseguramos
    private BigDecimal price; // El contenedor de precios y céntimos.

    @Column(name = "precio_original", precision = 9, scale = 2) 
    private BigDecimal originalPrice; // Si está de oferta, nos sirve para pintar la caja roja que dice: "Antes: XX".

    @NotNull(message = "El stock es obligatorio") 
    @Min(value = 0, message = "El stock no puede ser negativo") // Nada de tener "-3" zapatillas en el almacén.
    @Column(name = "cantidad_stock", nullable = false)
    private Integer stock; // Las unidades que sobran en la trastienda.

    @jakarta.persistence.Lob
    @Column(name = "url_imagen", columnDefinition = "MEDIUMTEXT")
    private String imageUrl;

    @Size(max = 30)
    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "rango_tallas", length = 20)
    private String sizeRange; // Ej: "38-46"

    @Column(name = "destacado", nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /** Calcula el porcentaje de descuento si hay precio original. */
    @Transient
    public int getDiscountPercentage() {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) return 0;
        if (price.compareTo(originalPrice) >= 0) return 0;
        BigDecimal diff = originalPrice.subtract(price);
        return diff.multiply(BigDecimal.valueOf(100))
                .divide(originalPrice, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}
