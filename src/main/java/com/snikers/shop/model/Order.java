package com.snikers.shop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Pedido realizado por un usuario.
 * Relaciones: N:1 con User, 1:N con OrderItem.
 */
@Entity
@Table(name = "pedidos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido", nullable = false, unique = true, length = 20)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PENDIENTE;

    @Column(name = "direccion_envio", length = 255)
    private String shippingAddress;

    // Método de pago elegido por el cliente al realizar el pedido.
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.EFECTIVO;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.orderNumber == null) {
            // UUID parcial: 4 letras prefijo + 12 caracteres hex → máximo 16 chars (columna length=20)
            this.orderNumber = "SNK-" + java.util.UUID.randomUUID().toString()
                    .replace("-", "").substring(0, 12).toUpperCase();
        }
    }

    public enum Status {
        PENDIENTE, PAGADO, ENVIADO, ENTREGADO, CANCELADO
    }

    public enum PaymentMethod {
        TARJETA, EFECTIVO
    }
}
