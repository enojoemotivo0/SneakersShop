package com.snikers.shop.model;

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
 * Producto (sneaker) de la tienda.
 * Relación N:1 con Category.
 */
@Entity
@Table(name = "productos", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_brand", columnList = "brand")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String brand;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 7, fraction = 2)
    @Column(nullable = false, precision = 9, scale = 2)
    private BigDecimal price;

    @Column(precision = 9, scale = 2)
    private BigDecimal originalPrice; // Para mostrar descuentos

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Size(max = 255)
    @Column(length = 255)
    private String imageUrl;

    @Size(max = 30)
    @Column(length = 30)
    private String color;

    @Column(length = 20)
    private String sizeRange; // Ej: "38-46"

    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
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
