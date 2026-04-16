package com.tienda.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 🎓 CLASE MODELO: Producto
 * Esta clase representa la tabla "productos" en nuestra base de datos.
 * Funciona como un molde (modelo) para crear y guardar los productos de nuestra tienda.
 */
@Entity // Le dice a Spring y a Hibernate (motor de base de datos) que esta clase es una tabla.
@Table(name = "productos") // (Opcional) Le ponemos un nombre específico a la tabla en la BD.
public class Producto {
    
    @Id // Indica que este campo es la Clave Primaria (Primary Key) de la tabla, su identificador único.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a la BD que genere automáticamente este ID de forma autoincremental (1, 2, 3...).
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío") // Validación: Obliga a que el nombre no sea solo espacios en blanco ni nulo.
    private String nombre;
    
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres") // Validación: Limita el tamaño del texto.
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio") // Validación: No puede ser nulo o vacío.
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0") // Validación: Evita que pongan precios negativos.
    private Double precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    // Campo para guardar la ruta de la imagen en nuestro servidor (ej: "/images/foto.jpg").
    private String imagenUrl;
    
    @NotBlank(message = "La talla es obligatoria")
    private String talla;
    
    // @ManyToOne indica que "Muchos productos pueden pertenecer a Una categoría". 
    // Relación de base de datos (N a 1).
    @ManyToOne
    @JoinColumn(name = "categoria_id") // Nombre de la columna en la tabla "productos" que guardará el ID de la categoría.
    @NotNull(message = "Debe seleccionar una categoría")
    private Categoria categoria;
    
    // Eliminamos la relación con Cliente, ya que un producto pertenece a la tienda y no a un cliente.
    
    // @OneToMany indica que "Un producto puede estar en Muchos detalles de pedido".
    // El "mappedBy" le dice a Spring que busque la variable "producto" en la otra clase (DetallePedido) para entender la relación.
    @jakarta.persistence.OneToMany(mappedBy = "producto", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<DetallePedido> detalles = new java.util.ArrayList<>();

    // Constructor vacío: Es obligatorio para que el framework de base de datos (Hibernate) pueda crear objetos internamente.
    public Producto() {}

    // ¡Abajo están los Getters y Setters!
    // Son necesarios para que Spring pueda leer (get) y modificar (set) los datos de este objeto.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public java.util.List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(java.util.List<DetallePedido> detalles) { this.detalles = detalles; }
}
