package com.snikers.shop.service;

import com.snikers.shop.dto.CartItem;
import com.snikers.shop.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compra guardado en sesión HTTP.
 */
@Service
@SessionScope
@RequiredArgsConstructor
public class CartService {

    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void add(Product product, int quantity, String size) {
        for (CartItem item : items) {
            if (item.getProductId().equals(product.getId())
                    && java.util.Objects.equals(item.getSize(), size)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(CartItem.builder()
                .productId(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .unitPrice(product.getPrice())
                .quantity(quantity)
                .size(size)
                .build());
    }

    public void remove(Long productId, String size) {
        items.removeIf(i -> i.getProductId().equals(productId)
                && java.util.Objects.equals(i.getSize(), size));
    }

    public void updateQuantity(Long productId, String size, int quantity) {
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)
                    && java.util.Objects.equals(item.getSize(), size)) {
                if (quantity <= 0) {
                    remove(productId, size);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
