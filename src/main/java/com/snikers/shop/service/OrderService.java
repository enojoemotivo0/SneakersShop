package com.snikers.shop.service;

import com.snikers.shop.dto.CartItem;
import com.snikers.shop.model.*;
import com.snikers.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public Order createFromCart(User user, List<CartItem> cartItems, String shippingAddress) {
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(Order.Status.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product product = productService.findById(ci.getProductId());
            productService.decrementStock(product.getId(), ci.getQuantity());
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice())
                    .size(ci.getSize())
                    .build();
            order.getItems().add(item);
            total = total.add(ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        order.setTotal(total);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    public Order updateStatus(Long id, Order.Status status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
