package com.snikers.shop.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.snikers.shop.model.Order;
import com.snikers.shop.model.OrderItem;

@Service
public class PdfService {

    public byte[] generateInvoice(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Document document = new Document(PageSize.A4, 50, 50, 50, 50)) {
            PdfWriter.getInstance(document, out);
            document.open();

            // Colores
            java.awt.Color primaryColor = new java.awt.Color(33, 37, 41); // Dark grey / Black
            java.awt.Color lightColor = new java.awt.Color(241, 243, 245); // Light grey para el fondo

            // Fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, primaryColor);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, primaryColor);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, java.awt.Color.DARK_GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, primaryColor);
            Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, java.awt.Color.WHITE);

            // Título
            Paragraph title = new Paragraph("SNIKERS SHOP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("FACTURA DE COMPRA", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30);
            document.add(subtitle);

            // Datos del pedido en una tabla para alinearlos mejor
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1f, 1f});
            infoTable.setSpacingAfter(30);

            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(PdfPCell.NO_BORDER);
            leftCell.addElement(new Paragraph("FACTURAR A:", boldFont));
            leftCell.addElement(new Paragraph(order.getUser().getFullName(), normalFont));
            leftCell.addElement(new Paragraph(order.getUser().getEmail(), normalFont));
            leftCell.addElement(new Paragraph(order.getShippingAddress(), normalFont));
            infoTable.addCell(leftCell);

            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(PdfPCell.NO_BORDER);
            rightCell.addElement(new Paragraph("DETALLES DEL PEDIDO:", boldFont));
            rightCell.addElement(new Paragraph("Nº de Pedido: " + order.getOrderNumber(), normalFont));
            rightCell.addElement(new Paragraph("Fecha: " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont));
            rightCell.addElement(new Paragraph("Método de Pago: Tarjeta Bancaria", normalFont)); // Ejemplo
            infoTable.addCell(rightCell);

            document.add(infoTable);

            // Tabla de productos
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4.5f, 1f, 2f, 2f});
            table.setSpacingBefore(10);

            // Cabecera de la tabla
            PdfPCell h1 = new PdfPCell(new Phrase("PRODUCTO", thFont));
            h1.setBackgroundColor(primaryColor);
            h1.setPadding(8);
            h1.setBorder(PdfPCell.NO_BORDER);
            table.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("CANT.", thFont));
            h2.setBackgroundColor(primaryColor);
            h2.setPadding(8);
            h2.setBorder(PdfPCell.NO_BORDER);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("PRECIO", thFont));
            h3.setBackgroundColor(primaryColor);
            h3.setPadding(8);
            h3.setBorder(PdfPCell.NO_BORDER);
            h3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Phrase("TOTAL", thFont));
            h4.setBackgroundColor(primaryColor);
            h4.setPadding(8);
            h4.setBorder(PdfPCell.NO_BORDER);
            h4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(h4);

            // Filas
            boolean alternate = false;
            for (OrderItem item : order.getItems()) {
                java.awt.Color bgColor = alternate ? lightColor : java.awt.Color.WHITE;

                String desc = item.getProduct().getName() + " (Talla: " + item.getSize() + ")";
                PdfPCell c1 = new PdfPCell(new Phrase(desc, normalFont));
                c1.setBackgroundColor(bgColor);
                c1.setPadding(8);
                c1.setBorderColor(java.awt.Color.LIGHT_GRAY);
                c1.setBorder(PdfPCell.BOTTOM);
                table.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                c2.setBackgroundColor(bgColor);
                c2.setPadding(8);
                c2.setHorizontalAlignment(Element.ALIGN_CENTER);
                c2.setBorderColor(java.awt.Color.LIGHT_GRAY);
                c2.setBorder(PdfPCell.BOTTOM);
                table.addCell(c2);

                PdfPCell c3 = new PdfPCell(new Phrase(item.getUnitPrice().toString() + " €", normalFont));
                c3.setBackgroundColor(bgColor);
                c3.setPadding(8);
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c3.setBorderColor(java.awt.Color.LIGHT_GRAY);
                c3.setBorder(PdfPCell.BOTTOM);
                table.addCell(c3);

                PdfPCell c4 = new PdfPCell(new Phrase(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())).toString() + " €", boldFont));
                c4.setBackgroundColor(bgColor);
                c4.setPadding(8);
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setBorderColor(java.awt.Color.LIGHT_GRAY);
                c4.setBorder(PdfPCell.BOTTOM);
                table.addCell(c4);

                alternate = !alternate;
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            // Total
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor);
            Paragraph total = new Paragraph("TOTAL A PAGAR: " + order.getTotal().toString() + " €", totalFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            // Pie de página
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Gracias por tu compra en Snikers Shop.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, java.awt.Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
        } catch (com.lowagie.text.DocumentException e) {
            throw new RuntimeException("Error al generar el PDF de la factura", e);
        }
        return out.toByteArray();
    }
}