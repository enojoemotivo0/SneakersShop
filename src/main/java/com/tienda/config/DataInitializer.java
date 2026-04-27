package com.tienda.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tienda.model.Categoria;
import com.tienda.model.Cliente;
import com.tienda.model.Producto;
import com.tienda.repository.ClienteRepository;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(CategoriaService categoriaService, 
                                      ProductoService productoService,
                                      ClienteRepository clienteRepository) {
        return args -> {
            // Categorias
            if (categoriaService.findAll().isEmpty()) {
                Categoria c1 = new Categoria();
                c1.setNombre("Zapatillas Urbanas");
                c1.setDescripcion("Estilo y comodidad para la ciudad");
                categoriaService.save(c1);

                Categoria c2 = new Categoria();
                c2.setNombre("Zapatillas Deportivas");
                c2.setDescripcion("Máximo rendimiento para entrenar");
                categoriaService.save(c2);

                // Productos
                Producto p1 = new Producto();
                p1.setNombre("Nike Air Force 1");
                p1.setDescripcion("Clásicas, blancas y versátiles");
                p1.setPrecio(119.99);
                p1.setStock(50);
                p1.setTalla("39, 40, 41, 42, 43");
                p1.setCategoria(c1);
                p1.setImagenUrl("https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7d9211c-26e7-431a-ac24-b0540fd3c27f/zapatillas-air-force-1-07-vNMZ10.png");
                productoService.save(p1);

                Producto p2 = new Producto();
                p2.setNombre("Adidas Ultraboost 22");
                p2.setDescripcion("Amortiguación perfecta para correr largas distancias");
                p2.setPrecio(180.00);
                p2.setStock(30);
                p2.setTalla("40, 41, 42, 44");
                p2.setCategoria(c2);
                p2.setImagenUrl("https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/208cb13ba04142f3844dae290119e8cf_9366/Zapatillas_Ultraboost_22_Negro_GZ0127_01_standard.jpg");
                productoService.save(p2);

                Producto p3 = new Producto();
                p3.setNombre("Jordan 1 Retro High");
                p3.setDescripcion("El ícono que lo empezó todo. Colorway clásico.");
                p3.setPrecio(210.00);
                p3.setStock(15);
                p3.setTalla("41, 42, 43, 44, 45, 46");
                p3.setCategoria(c1);
                p3.setImagenUrl("https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/a1c1d0f5-568e-4a6f-b25b-8f77342fb821/calzado-air-jordan-1-retro-high-og-M1Fv8v.png");
                productoService.save(p3);

                Producto p4 = new Producto();
                p4.setNombre("New Balance 550");
                p4.setDescripcion("Comodidad urbana con diseño retro de los 90s.");
                p4.setPrecio(140.00);
                p4.setStock(50); // Le damos stock para que siempre se pueda comprar
                p4.setTalla("38, 39, 40, 41");
                p4.setCategoria(c1);
                p4.setImagenUrl("https://nb.scene7.com/is/image/NB/bb550wt1_nb_02_i?$dw_detail_main_lg$&bgc=f1f1f1&layer=1&bgcolor=f1f1f1&blendMode=mult&scale=10&wid=1600&hei=1600");
                productoService.save(p4);
            }

            // Clientes
            if (clienteRepository.count() == 0) {
                Cliente cli1 = new Cliente();
                cli1.setNombre("Cliente VIP");
                cli1.setTipo("VIP");
                clienteRepository.save(cli1);

                Cliente cli2 = new Cliente();
                cli2.setNombre("Cliente Premium");
                cli2.setTipo("Premium");
                clienteRepository.save(cli2);

                Cliente cli3 = new Cliente();
                cli3.setNombre("Cliente Regular");
                cli3.setTipo("Regular");
                clienteRepository.save(cli3);
            }
        };
    }
}
