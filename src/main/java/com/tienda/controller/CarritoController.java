package com.tienda.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tienda.model.Cliente;
import com.tienda.model.DetallePedido;
import com.tienda.model.Pedido;
import com.tienda.model.Producto;
import com.tienda.repository.ClienteRepository;
import com.tienda.repository.DetallePedidoRepository;
import com.tienda.repository.PedidoRepository;
import com.tienda.repository.ProductoRepository;
import com.tienda.service.FacturaPdfService;
import com.tienda.service.ProductoService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private FacturaPdfService facturaPdfService;

    @PostMapping("/add")
    public String addCart(@RequestParam("id") Long id, @RequestParam("cantidad") Integer cantidad, 
                          @RequestParam(value = "talla", required = false) String talla, HttpSession session) {
        
        Optional<Producto> optionalProducto = productoService.findById(id);
        if (optionalProducto.isEmpty()) {
            return "redirect:/";
        }
        Producto producto = optionalProducto.get();

        Pedido pedido = obtenerPedidoDeSesion(session);

        // Buscar si el producto ya está en el carrito con la MISMA TALLA
        Optional<DetallePedido> dOptional = pedido.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(id) && 
                             (talla == null ? d.getTalla() == null : talla.equals(d.getTalla())))
                .findFirst();

        if (dOptional.isPresent()) {
            DetallePedido detalle = dOptional.get();
            int nuevaCantidad = detalle.getCantidad() + cantidad;
            if (nuevaCantidad > producto.getStock()) {
                return "redirect:/carrito?error=Stock_insuficiente";
            }
            detalle.setCantidad(nuevaCantidad);
            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecio());
            detallePedidoRepository.save(detalle); // guardar en BD real
        } else {
            if (cantidad > producto.getStock()) {
                return "redirect:/carrito?error=Stock_insuficiente";
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setCantidad(cantidad);
            detalle.setPrecio(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * cantidad);
            detalle.setProducto(producto);
            detalle.setPedido(pedido);
            detalle.setTalla(talla);
            
            pedido.getDetalles().add(detalle);
            detallePedidoRepository.save(detalle); // guardar en BD real
        }

        actualizarTotalPedido(pedido);

        return "redirect:/carrito";
    }

    @GetMapping("")
    public String verCarrito(Model model, HttpSession session) {
        Pedido pedido = obtenerPedidoDeSesion(session);
        model.addAttribute("detalles", pedido.getDetalles());
        model.addAttribute("pedido", pedido);
        return "carrito/vista";
    }

    @GetMapping("/delete/{id}")
    @SuppressWarnings("null")
    public String eliminarDelCarrito(@PathVariable Long id, HttpSession session) {
        Pedido pedido = obtenerPedidoDeSesion(session);
        
        Optional<DetallePedido> detalleOpt = detallePedidoRepository.findById(id);
        if (detalleOpt.isPresent() && detalleOpt.get().getPedido().getId().equals(pedido.getId())) {
            DetallePedido detalle = detalleOpt.get();
            pedido.getDetalles().removeIf(d -> d.getId().equals(detalle.getId()));
            actualizarTotalPedido(pedido);
            
            detallePedidoRepository.deleteById(id);
        }
        
        return "redirect:/carrito";
    }

    @PostMapping("/updateTalla")
    @SuppressWarnings("null")
    public String updateTalla(@RequestParam("id") Long id, @RequestParam("talla") String talla, HttpSession session) {
        Pedido pedido = obtenerPedidoDeSesion(session);
        Optional<DetallePedido> detalleOpt = detallePedidoRepository.findById(id);
        
        if (detalleOpt.isPresent() && detalleOpt.get().getPedido().getId().equals(pedido.getId())) {
            DetallePedido detalle = detalleOpt.get();
            
            // Comprobamos si ya existe otro detalle con este mismo producto pero con la nueva talla
            Optional<DetallePedido> existenteOpt = pedido.getDetalles().stream()
                    .filter(d -> !d.getId().equals(id) && 
                                 d.getProducto().getId().equals(detalle.getProducto().getId()) && 
                                 talla.equals(d.getTalla()))
                    .findFirst();

            if (existenteOpt.isPresent()) {
                // Si existe, combinamos las cantidades
                DetallePedido existente = existenteOpt.get();
                int nuevaCantidad = existente.getCantidad() + detalle.getCantidad();
                if (nuevaCantidad > existente.getProducto().getStock()) {
                    return "redirect:/carrito?error=Stock_insuficiente";
                }
                
                existente.setCantidad(nuevaCantidad);
                existente.setSubtotal(existente.getCantidad() * existente.getPrecio());
                detallePedidoRepository.save(existente);
                
                // Eliminamos el detalle viejo
                pedido.getDetalles().removeIf(d -> d.getId().equals(detalle.getId()));
                detallePedidoRepository.deleteById(id);
                
                actualizarTotalPedido(pedido);
            } else {
                // Si no existe uno igual con la talla nueva, simplemente actualizamos la talla
                detalle.setTalla(talla);
                detallePedidoRepository.save(detalle);
                // No hace falta actualizar el total porque el precio / cantidad no cambia
            }
        }
        
        return "redirect:/carrito";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        Pedido pedido = obtenerPedidoDeSesion(session);
        if (pedido.getDetalles().isEmpty()) {
            return "redirect:/carrito"; // Si no hay nada, regresa al carrito
        }
        model.addAttribute("pedido", pedido);
        return "carrito/checkout";
    }

    @PostMapping("/procesarPago")
    public String procesarPago(@RequestParam("nombre") String nombre, 
                               @RequestParam("direccion") String direccion,
                               HttpSession session, Model model) {
        Pedido pedido = obtenerPedidoDeSesion(session);
        
        if (pedido.getDetalles().isEmpty()) {
            return "redirect:/carrito";
        }

        // Crear Cliente y asociarlo a este Pedido
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setDireccion(direccion);
        cliente.setTipo("Comprador Web");
        cliente = clienteRepository.save(cliente);

        pedido.setCliente(cliente);
        
        // Reducir stock y Simulamos la compra marcando el pedido como pagado
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto p = detalle.getProducto();
            p.setStock(p.getStock() - detalle.getCantidad());
            productoRepository.save(p);
        }

        pedido.setEstado("COMPLETADO");
        pedido.setFecha(new Date()); // Fecha de la compra real
        pedidoRepository.save(pedido);
        
        // Removemos el carrito de la memoria de la sesión
        session.removeAttribute("pedido_id");
        
        // Pasamos el ID a la vista para confirmar
        model.addAttribute("pedidoId", pedido.getId());
        model.addAttribute("pedidoTotal", pedido.getTotal());
        return "carrito/exito";
    }

    @GetMapping("/factura/{id}")
    @SuppressWarnings("null")
    public void exportarPdfFactura(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        
        if (pedidoOpt.isPresent() && "COMPLETADO".equals(pedidoOpt.get().getEstado())) {
            response.setContentType("application/pdf");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());
            
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=factura_" + id + "_" + currentDateTime + ".pdf";
            response.setHeader(headerKey, headerValue);
            
            facturaPdfService.exportar(response, pedidoOpt.get());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Factura no encontrada o pedido no completado");
        }
    }

    // Helper metod para recuperar (o crear) el pedido en la BD logado a la sesion
    private Pedido obtenerPedidoDeSesion(HttpSession session) {
        Long pedidoId = (Long) session.getAttribute("pedido_id");
        Pedido pedido = null;

        if (pedidoId != null) {
            pedido = pedidoRepository.findById((long) pedidoId).orElse(null);
        }

        if (pedido == null || !"CARRITO".equals(pedido.getEstado())) {
            pedido = new Pedido();
            pedido.setFecha(new Date());
            pedido.setEstado("CARRITO");
            pedido.setTotal(0.0);
            pedido = pedidoRepository.save(pedido); // Se guarda vacío en la BD de inmediato
            session.setAttribute("pedido_id", pedido.getId());
        }

        return pedido;
    }

    private void actualizarTotalPedido(Pedido pedido) {
        double total = pedido.getDetalles().stream().mapToDouble(DetallePedido::getSubtotal).sum();
        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }
}
