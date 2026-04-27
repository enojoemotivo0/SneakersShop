package com.snikers.shop.service;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.snikers.shop.model.Product;

/**
 * Tests unitarios del carrito de compra.
 * No requieren contexto Spring: CartService se instancia directamente.
 */
class CartServiceTest {

    private CartService cartService;
    private Product product;

    @BeforeEach
    public void setUp() {
        cartService = new CartService();
        // Producto de prueba con 5 unidades en stock
        product = Product.builder()
                .id(1L)
                .name("Air Max Test")
                .brand("TestBrand")
                .price(new BigDecimal("120.00"))
                .stock(5)
                .build();
    }

    @Test
    void add_creaItemNuevo_cuandoNoExisteEnCarrito() {
        cartService.add(product, 1, "42");

        assertThat(cartService.getItems()).hasSize(1);
        assertThat(cartService.getItems().get(0).getQuantity()).isEqualTo(1);
        assertThat(cartService.getItems().get(0).getName()).isEqualTo("Air Max Test");
    }

    @Test
    void add_acumulaCantidad_cuandoMismoProductoYTalla() {
        cartService.add(product, 2, "42");
        cartService.add(product, 1, "42");

        // Debe ser un único item con cantidad acumulada
        assertThat(cartService.getItems()).hasSize(1);
        assertThat(cartService.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void add_creaItemSeparado_cuandoDistintaTalla() {
        cartService.add(product, 1, "41");
        cartService.add(product, 1, "42");

        // Tallas diferentes = líneas separadas en el carrito
        assertThat(cartService.getItems()).hasSize(2);
    }

    @Test
    void add_lanzaExcepcion_cuandoStockInsuficiente() {
        // Stock = 5, pedimos 10 → debe fallar
        assertThatThrownBy(() -> cartService.add(product, 10, "42"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void add_lanzaExcepcion_cuandoStockInsuficienteAcumulado() {
        // Añadimos 4 (OK), luego 2 más → 6 > 5 → excepción
        cartService.add(product, 4, "42");

        assertThatThrownBy(() -> cartService.add(product, 2, "42"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void getTotal_calculaCorrectamente() {
        cartService.add(product, 2, "42"); // 2 × 120,00 = 240,00

        assertThat(cartService.getTotal()).isEqualByComparingTo("240.00");
    }

    @Test
    void remove_eliminaItemDelCarrito() {
        cartService.add(product, 2, "42");
        cartService.remove(1L, "42");

        assertThat(cartService.getItems()).isEmpty();
    }

    @Test
    void clear_vaciaTodosLosItems() {
        cartService.add(product, 1, "41");
        cartService.add(product, 1, "42");
        cartService.clear();

        assertThat(cartService.isEmpty()).isTrue();
        assertThat(cartService.getTotalItems()).isEqualTo(0);
    }
}
