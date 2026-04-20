package com.snikers.shop.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    private Long productId;
    private String name;
    private String brand;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String size;

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
