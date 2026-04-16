package com.tienda.service;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tienda.model.DetallePedido;
import com.tienda.model.Pedido;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class FacturaPdfService {

    public void exportar(HttpServletResponse response, Pedido pedido) throws IOException {
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, response.getOutputStream());
            
            document.open();
            
            // Fuentes
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitulo.setSize(18);
            fontTitulo.setColor(Color.BLUE);
            
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA);
            fontNormal.setSize(12);

            // Título
            Paragraph p = new Paragraph("Factura de Compra - SneakersShop", fontTitulo);
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);
            
            // Datos del Pedido y Cliente
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String fecha = formateador.format(pedido.getFecha());
            
            Paragraph datos = new Paragraph();
            datos.add(new Phrase("\nNº Pedido: " + pedido.getId() + "\n", fontNormal));
            datos.add(new Phrase("Fecha: " + fecha + "\n", fontNormal));
            if (pedido.getCliente() != null) {
                datos.add(new Phrase("Cliente: " + pedido.getCliente().getNombre() + "\n", fontNormal));
                datos.add(new Phrase("Dirección: " + pedido.getCliente().getDireccion() + "\n\n", fontNormal));
            } else {
                datos.add(new Phrase("Cliente: Consumidor Final\n\n", fontNormal));
            }
            document.add(datos);

            // Tabla de Productos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new float[] {4.0f, 2.0f, 2.0f, 2.0f});
            table.setSpacingBefore(10);
            
            formatearCabeceraTabla(table);
            escribirDatosTabla(table, pedido);
            
            document.add(table);
            
            // Total
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTotal.setSize(14);
            fontTotal.setColor(Color.RED);
            Paragraph total = new Paragraph("\nTotal Pagado: $" + String.format("%.2f", pedido.getTotal()), fontTotal);
            total.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(total);
        }
    }
    
    private void formatearCabeceraTabla(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);
        
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
        
        cell.setPhrase(new Phrase("Producto", font));
        table.addCell(cell);
        
        cell.setPhrase(new Phrase("Precio U.", font));
        table.addCell(cell);
        
        cell.setPhrase(new Phrase("Cantidad", font));
        table.addCell(cell);
        
        cell.setPhrase(new Phrase("Subtotal", font));
        table.addCell(cell);
    }
    
    private void escribirDatosTabla(PdfPTable table, Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            String nombreProducto = detalle.getProducto().getNombre();
            if (detalle.getTalla() != null && !detalle.getTalla().trim().isEmpty()) {
                nombreProducto += " (Talla: " + detalle.getTalla() + ")";
            }
            table.addCell(nombreProducto);
            table.addCell("$" + String.format("%.2f", detalle.getPrecio()));
            table.addCell(String.valueOf(detalle.getCantidad()));
            table.addCell("$" + String.format("%.2f", detalle.getSubtotal()));
        }
    }
}