package com.snikers.shop.config;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snikers.shop.controller.HomeController;
import com.snikers.shop.service.CartService;
import com.snikers.shop.service.CategoryService;
import com.snikers.shop.service.ProductService;
import com.snikers.shop.service.UserService;

/**
 * Tests de configuración de seguridad.
 * Verifica que las rutas protegidas requieren autenticación y redirigen al login.
 */
@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

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

    @BeforeEach
    public void init() {
        // Resetea los mocks antes de cada test (los hace "usados" y evita estado compartido)
        Mockito.reset(cartService, userService);
    }

    @Test
    void adminSinAutenticacion_redireccionaAlLogin() throws Exception {
        // Spring Security intercepta antes del controller → 302 hacia /login
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void adminDashboardSinAutenticacion_redireccionaAlLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void rutaPublica_accesibleSinAutenticacion() throws Exception {
        given(productService.findFeatured()).willReturn(Collections.emptyList());
        given(categoryService.findAll()).willReturn(Collections.emptyList());

        // La ruta "/" es pública → debe devolver 200
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
