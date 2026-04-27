package com.snikers.shop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.snikers.shop.dto.CartItem;
import com.snikers.shop.model.Order;
import com.snikers.shop.model.OrderItem;
import com.snikers.shop.model.Product;
import com.snikers.shop.model.User;
import com.snikers.shop.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public Order createFromCart(User user, List<CartItem> cartItems, String shippingAddress, Order.PaymentMethod paymentMethod) {
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(Order.Status.PENDIENTE)
                .paymentMethod(paymentMethod)
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
        Long safeId = Objects.requireNonNull(id, "El id del pedido no puede ser null");
        return orderRepository.findById(safeId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    public Order updateStatus(Long id, Order.Status status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
