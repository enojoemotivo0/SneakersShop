package com.snikers.shop.controller;

import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;
import com.snikers.shop.config.SecurityConfig;
import com.snikers.shop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de la capa web para HomeController.
 * Verifica que las rutas públicas devuelven 200 y la vista correcta.
 */
@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CartService cartService;

    // Necesario para que SecurityConfig pueda construir DaoAuthenticationProvider
    @MockBean
    private UserService userService;

    @Test
    void getHome_retorna200YVistaHome() throws Exception {
        given(productService.findFeatured()).willReturn(Collections.emptyList());
        given(categoryService.findAll()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void getHomeAlternativo_retorna200YVistaHome() throws Exception {
        given(productService.findFeatured()).willReturn(Collections.emptyList());
        given(categoryService.findAll()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }
}
