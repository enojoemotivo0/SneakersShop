# 📚 MEMORIA DE LA APLICACIÓN — SNIKERS SHOP

**Proyecto Transversal Final — 2º DAM/DAW**  
**Arquitectura**: Spring Boot MVC + Thymeleaf + MySQL + Spring Security  
**Última actualización**: Abril 2026

---

## 🎯 DESCRIPCIÓN GENERAL

**SNIKERS SHOP** es una tienda de comercio electrónico especializada en venta de sneakers (zapatillas deportivas). Permite a usuarios registrados navegar por un catálogo, filtrar por categoría, buscar productos, añadirlos al carrito y completar una compra online. Incluye un panel administrativo para gestionar productos, categorías, pedidos y usuarios.

### Características principales:
- ✅ Catálogo dinámico con paginación y búsqueda
- ✅ Autenticación y registro de usuarios (BCrypt)
- ✅ Carrito de compra con persistencia en sesión
- ✅ Proceso de checkout con actualización de perfil
- ✅ Generación de facturas en PDF
- ✅ Panel administrativo completo (CRUD)
- ✅ Base de datos MySQL con relaciones 1:N
- ✅ Frontend premium con diseño responsive

---

## 🏗️ ARQUITECTURA MVC (3 CAPAS)

```
┌─────────────────────────────────────────┐
│         VISTA (Thymeleaf HTML)          │
│  (plantillas/* → genera páginas web)    │
└────────────────┬────────────────────────┘
                 ▲
                 │ (pasa datos)
                 │
┌────────────────▼────────────────────────┐
│    CONTROLADOR (8 clases)               │
│ (recibe peticiones HTTP → delega)       │
└────────────────┬────────────────────────┘
                 ▲
                 │ (usa servicios)
                 │
┌────────────────▼────────────────────────┐
│    SERVICIO (6 clases)                  │
│ (lógica de negocio → accede datos)      │
└────────────────┬────────────────────────┘
                 ▲
                 │ (queries)
                 │
┌────────────────▼────────────────────────┐
│    REPOSITORIO (4 interfaces)           │
│ (acceso a BD mediante Spring Data JPA)  │
└────────────────┬────────────────────────┘
                 ▲
                 │ (SQL auto generado)
                 │
┌────────────────▼────────────────────────┐
│    BASE DE DATOS (MySQL)                │
│ (5 tablas: usuarios, productos, etc.)   │
└─────────────────────────────────────────┘
```

---

## 📦 MODELOS/ENTIDADES (5 clases)

### 1. **USUARIO (User.java)**
```java
@Entity
@Table(name = "usuarios")
public class User {
    - id: Long (PK)
    - email: String (UNIQUE) → credencial para login
    - password: String (encriptada con BCrypt)
    - fullName: String
    - phone: String
    - address: String (dirección de envío)
    - profilePicture: String (Base64)
    - role: Enum {CLIENTE, ADMINISTRADOR}
    - createdAt: LocalDateTime
    - updatedAt: LocalDateTime
}
```
**Responsabilidades:**
- Almacena credenciales de acceso
- Identifica si es cliente normal o administrador
- Conserva dirección y datos de contacto

---

### 2. **CATEGORÍA (Category.java)**
```java
@Entity
@Table(name = "categorias")
public class Category {
    - id: Long (PK)
    - name: String (UNIQUE) → ej: "Running", "Basketball", "Skate"
    - description: String
    - createdAt: LocalDateTime
    ↓ 1:N
    - products: List<Product> (todos los productos de esta categoría)
}
```
**Responsabilidades:**
- Organiza los productos en grupos temáticos
- Permite filtrar en el catálogo
- Genera menús de navegación

---

### 3. **PRODUCTO (Product.java)**
```java
@Entity
@Table(name = "productos")
public class Product {
    - id: Long (PK)
    - name: String (ej: "Air Jordan 1")
    - brand: String (ej: "Nike", "Adidas")
    - description: String (texto largo con especificaciones)
    - price: BigDecimal (precio actual, ej: 129.99€)
    - originalPrice: BigDecimal (antes del descuento, para calcular %)
    - stock: Integer (unidades disponibles)
    - imageUrl: String (Base64 embebida en MEDIUMTEXT)
    - color: String
    - sizeRange: String (ej: "38-46")
    - featured: Boolean (¿aparece en portada?)
    - active: Boolean (¿visible en tienda?)
    - createdAt, updatedAt: LocalDateTime
    ↓ N:1
    - category: Category (a qué categoría pertenece)
}
```
**Responsabilidades:**
- Representa una zapatilla concreta en la tienda
- Mantiene stock e información comercial
- Calcula descuentos automáticamente

**Método especial:**
```java
public int getDiscountPercentage() {
    // Si hay originalPrice, calcula: ((original - actual) / original) * 100
    // Ejemplo: 200€ → 150€ = 25% de descuento
}
```

---

### 4. **PEDIDO (Order.java)**
```java
@Entity
@Table(name = "pedidos")
public class Order {
    - id: Long (PK)
    - orderNumber: String (UNIQUE, ej: "PED-2026-00001")
    - status: Enum {PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO}
    - shippingAddress: String
    - totalAmount: BigDecimal (suma de todos los items)
    - createdAt: LocalDateTime
    ↓ N:1
    - user: User (quién realizó el pedido)
    ↓ 1:N
    - items: List<OrderItem> (qué productos pidió)
}
```
**Responsabilidades:**
- Representa una transacción de compra completa
- Almacena el historial de pedidos del cliente
- Genera facturas

---

### 5. **LÍNEA DE PEDIDO (OrderItem.java)**
```java
@Entity
@Table(name = "detalles_pedido")
public class OrderItem {
    - id: Long (PK)
    - quantity: Integer (cuántas unidades de este producto)
    - size: String (talla seleccionada, ej: "42")
    - priceAtPurchase: BigDecimal (precio en el momento de compra)
    - subtotal: BigDecimal (priceAtPurchase * quantity)
    ↓ N:1
    - order: Order
    ↓ N:1
    - product: Product
}
```
**Responsabilidades:**
- Desglose detallado de cada línea de un pedido
- Preserva el precio en el momento de compra (útil si después baja)
- Registra la talla seleccionada

---

## 🔧 SERVICIOS (6 clases)

### **1. ProductService**
**¿Qué hace?** Lógica de negocio para gestionar zapatillas.

**Métodos principales:**
```java
// LECTURA
findAllActive()                           // Todos los productos visibles
findAllActive(Pageable pageable)          // Con paginación (12 por página)
findFeatured()                            // Solo destacados para portada
findById(Long id)                         // Una zapatilla específica
findByCategory(Long categoryId, Pageable) // Filtrar por categoría
search(String query, Pageable)            // Buscar por nombre/marca/descripción
count()                                   // Contar totales

// ESCRITURA
save(Product product)                     // Crear nuevo producto
update(Long id, Product updated)          // Modificar existente
softDelete(Long id)                       // Baja lógica (active = false)
decrementStock(Long productId, int qty)   // Restar stock al comprar
```

---

### **2. CategoryService**
**¿Qué hace?** Gestiona las categorías de zapatillas.

**Métodos:**
```java
findAll()                        // Listado de categorías
findById(Long id)                // Una categoría específica
save(Category category)          // Crear nueva
update(Long id, Category updated) // Modificar
delete(Long id)                  // Eliminar
```

---

### **3. UserService**
**¿Qué hace?** Gestiona usuarios y autenticación.

**Métodos:**
```java
loadUserByUsername(String email)  // Para Spring Security (valida login)
register(User user)               // Alta de nuevo usuario
  ✓ Valida que no exista ese email
  ✓ Encripta la contraseña con BCrypt
  ✓ Asigna rol CLIENTE por defecto
findByEmail(String email)         // Busca usuario por correo
update(User user)                 // Actualiza perfil
```

**Encriptación de contraseña:**
```
Contraseña en texto plano: "1234"
Tras BCrypt: "$2a$10$vNlA7D8Z...N9M8L7K6J5"
↓
Cuando introduces "1234" al login, BCrypt comprueba sin desencriptar.
Es imposible saber la contraseña original (seguridad máxima).
```

---

### **4. CartService**
**¿Qué hace?** Gestiona el carrito de compra en sesión.

**Métodos:**
```java
add(Product p, int qty, String size)      // Añadir producto al carrito
getItems()                                 // Ver todos los items del carrito
getTotalItems()                            // Contar total de unidades
getTotal()                                 // Sumar precio total
updateQuantity(Long productId, String size, int qty) // Cambiar cantidad
remove(Long productId, String size)       // Eliminar una línea
clear()                                    // Vaciar carrito
isEmpty()                                  // ¿Está vacío?
```

**Estructura interna (en sesión HTTP):**
```java
@Scope("session")  // Cada usuario tiene su carrito mientras está conectado
private List<CartItem> items;  // Qué productos tiene
```

---

### **5. OrderService**
**¿Qué hace?** Crea y gestiona pedidos.

**Métodos:**
```java
createFromCart(User user, List<CartItem> items, String address)
    // Transforma el carrito en un pedido guardado en BD
    ✓ Genera orderNumber único (PED-2026-00001)
    ✓ Decrementa stock de cada producto
    ✓ Guarda orden y detalles
    ✓ Retorna Order creada

findById(Long id)                      // Buscar pedido por ID
findByUser(Long userId)                // Todos los pedidos de un cliente
updateStatus(Long id, Order.Status s)  // Cambiar estado (PENDIENTE→ENVIADO)
findAll()                              // Listar todos (solo admin)
```

---

### **6. PdfService**
**¿Qué hace?** Genera facturas en PDF.

**Métodos:**
```java
generateInvoice(Order order)  // Crea PDF con:
    ✓ Número de pedido
    ✓ Datos del cliente
    ✓ Detalles de cada línea
    ✓ Total y fecha
    ✓ Logo de la tienda
```

---

## 🗂️ REPOSITORIOS (4 interfaces)

**¿Qué son?** Interfaces que extienden `JpaRepository`. Generan automáticamente SQL para acceder a BD sin escribir consultas manualmente.

### **1. ProductRepository**
```java
extends JpaRepository<Product, Long>

// Spring genera automáticamente (sin escribir SQL):
findByActiveTrue()                        // SELECT * FROM productos WHERE activo = true
findByActiveTrue(Pageable p)              // (CON paginación)
findByFeaturedTrueAndActiveTrue()         // WHERE destacado=true AND activo=true
findByCategoryIdAndActiveTrue(Long id, Pageable p)  // Filtro por categoría
search(String query, Pageable)            // CUSTOM: busca en nombre, marca, descripción
countByActiveTrue()                       // Contar

// Se define así:
@Query(value = "SELECT p FROM Product p WHERE 
    (LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR 
     LOWER(p.brand) LIKE LOWER(CONCAT('%',:q,'%')) OR 
     LOWER(p.description) LIKE LOWER(CONCAT('%',:q,'%'))) 
    AND p.active = true", ...)
Page<Product> search(@Param("q") String query, Pageable pageable);
```

---

### **2. UserRepository**
```java
extends JpaRepository<User, Long>

findByEmail(String email)      // Buscar por correo (UNIQUE)
existsByEmail(String email)    // ¿Ya existe ese email?
```

---

### **3. OrderRepository**
```java
extends JpaRepository<Order, Long>

findByUserId(Long userId)      // Todos los pedidos de un cliente
findAll()                      // Todos (sin paginación)
```

---

### **4. CategoryRepository**
```java
extends JpaRepository<Category, Long>

findAll()                      // Todas las categorías
findById(Long id)              // Una por ID
```

---

## 🎛️ CONTROLADORES (8 clases)

**¿Qué son?** Puntos de entrada HTTP. Reciben peticiones del navegador, usan servicios y devuelven vistas.

### **1. HomeController**
**URL**: `/` y `/home`

```java
@GetMapping({"/", "/home"})
public String home(Model model) {
    // Busca productos destacados
    model.addAttribute("featuredProducts", productService.findFeatured());
    // Carga todas las categorías
    model.addAttribute("categories", categoryService.findAll());
    // Datos para SEO (Google)
    model.addAttribute("pageTitle", "SNIKERS — Sneakers premium...");
    return "home";  // Renderiza: templates/home.html
}
```

---

### **2. ProductController**
**URLs**: `/products`, `/products/{id}`, `/search`

```java
@GetMapping("/products")
// Listado con paginación y filtros
// Parámetros:
//   - categoryId (opcional): filtrar por categoría
//   - q (opcional): búsqueda textual
//   - page (defecto 0): número de página
//   - size (defecto 12): items por página
//   - sort (defecto createdAt,desc): ordenamiento

@GetMapping("/products/{id}")
// Detalle de un producto
// ¿Qué pasa si el ID no existe?
// → Lanza IllegalArgumentException → GlobalExceptionHandler → Página error

@GetMapping("/search")
// Conveniencia: si escribes en la barra de búsqueda, redirige a /products?q=...
```

---

### **3. AuthController**
**URLs**: `/login`, `/register`

```java
@GetMapping("/login")
// Muestra formulario de login
// Si hay parámetro "error=true" → muestra mensaje rojo
// Si hay "logout=true" → muestra "Sesión cerrada"

@PostMapping("/register")
// Alta de nuevo usuario
// Valida datos (@Valid)
// Sube foto de perfil si existe → conviertea Base64
// Si email ya existe → error "email.duplicate"
// Redirige a /login?registered=true
```

---

### **4. CartController**
**URLs**: `/cart`

```java
@GetMapping
public String view(Model model)         // Ver carrito actual

@PostMapping("/add")
// Parámetros: productId, quantity (defecto 1), size (opcional talla)
// Actualiza la lista de sesión

@PostMapping("/update")
// Cambiar cantidad de una línea

@PostMapping("/remove")
// Eliminar una línea del carrito

@PostMapping("/clear")
// Vaciar todo
```

---

### **5. CheckoutController**
**URL**: `/checkout`

```java
@GetMapping
// Muestra formulario de checkout pre-rellenado con datos del usuario

@PostMapping
// 1. Valida que el carrito no esté vacío
// 2. Actualiza datos del usuario si cambió (email, teléfono, etc.)
// 3. Si cambió email → refresca sesión de Spring Security
// 4. Crea el Order desde el carrito
// 5. Vacía el carrito
// 6. Redirige a /orders/{id}?confirmed=true
```

---

### **6. OrderController**
**URLs**: `/orders`, `/orders/{id}`, `/orders/{id}/invoice`

```java
@GetMapping
// Lista tus pedidos (solo si estás autenticado)

@GetMapping("/{id}")
// Detalle de un pedido
// Validación de seguridad:
//   - Solo si eres el propietario O eres ADMIN

@GetMapping("/{id}/invoice")
// Descarga PDF de la factura
// Misma validación de seguridad
```

---

### **7. AdminController**
**URL**: `/admin` (solo acceso con rol ADMINISTRADOR)

```
📊 /admin               Dashboard con métricas

📦 /admin/products      Listado de productos
   /products/new       Formulario crear producto
   /products/{id}/edit Editar producto
   /products/{id}/delete Borrar (baja lógica)

📂 /admin/categories    Gestión de categorías
   /categories         Crear/listar

📋 /admin/orders        Ver todos los pedidos
   /orders/{id}/status Cambiar estado

👥 /admin/users         Gestión de usuarios
   /users              Crear/listar
```

---

### **8. GlobalExceptionHandler**
**¿Qué hace?** Atrapa excepciones no controladas y muestra páginas de error amigables.

```java
@ControllerAdvice  // Aplica a TODOS los controladores

@ExceptionHandler(IllegalArgumentException.class)
// ¿Producto no existe? (404)

@ExceptionHandler(IllegalStateException.class)
// ¿Stock insuficiente? (400)

@ExceptionHandler(Exception.class)
// Cualquier otra (500)
```

---

## 🔐 SEGURIDAD (SecurityConfig.java)

### **Encriptación de contraseñas (BCrypt)**
```
Entrada: "miContraseña123"
           ↓
      BCryptPasswordEncoder.encode()
           ↓
Almacenado: "$2a$10$N9L8M7K6J5I4H3G2F1E0D9C8B7A6Z5Y4X3W2V1U0T9S8R7Q6P5O4N3M2"

Al hacer login:
Entrada: "miContraseña123"
           ↓
      BCryptPasswordEncoder.matches(input, stored)
           ↓
¿Coincide? ✅ SÍ → Acceso concedido
```

### **Rutas públicas vs privadas**
```
🟢 PÚBLICAS (sin login):
   / /home /products /categories /search /register /login /css /js

🔴 PRIVADAS (requieren @authenticated):
   /checkout /profile /orders

🔐 SOLO ADMIN:
   /admin/*
```

### **Ciclo de autenticación**
```
1. Usuario entra en /login
2. Introduce email y contraseña
3. Submit → POST /login
4. SecurityConfig comprueba en UserRepository
5. ✅ Si coincide → genera sesión (JSESSIONID)
6. ❌ Si no → redirige a /login?error=true
7. Con sesión activa → acceso a rutas @authenticated
8. /logout → destruye sesión + borra cookies
```

---

## 🔄 FLUJOS PRINCIPALES

### **Flujo 1: Navegación del cliente**
```
1. Cliente entra en "/" (HomeController)
   ↓ Carga productService.findFeatured() [portada con destacados]
   ↓ Muestra template: templates/home.html

2. Cliente hace clic en "Catálogo"
   ↓ Navega a "/products" (ProductController)
   ↓ Busca productService.findAllActive(Pageable)
   ↓ Renderiza: templates/products/list.html

3. Cliente filtra por categoría (ej: "Running")
   ↓ URL: "/products?categoryId=2"
   ↓ Busca productService.findByCategory(2, ...)
   ↓ Misma plantilla, pero con solo zapatillas Running

4. Cliente hace búsqueda ("Air Jordan")
   ↓ URL: "/search?q=Air+Jordan"
   ↓ Busca productService.search("Air Jordan", ...)
   ↓ Muestra resultados coincidentes

5. Cliente abre detalle de producto (ej: ID=5)
   ↓ URL: "/products/5"
   ↓ Busca productService.findById(5)
   ↓ Renderiza: templates/products/detail.html
   ↓ Muestra nombre, precio, descripción, stock, foto
```

---

### **Flujo 2: Compra (lo más importante)**
```
1️⃣ AGREGAR AL CARRITO
   Cliente ve zapatilla "Air Jordan" (ID=7)
   ↓ Selecciona talla "42" y cantidad "1"
   ↓ Hace clic en "Añadir al carrito"
   ↓ POST /cart/add?productId=7&quantity=1&size=42
   ↓ CartController.add() → cartService.add()
   ↓ Añade a sesión: {Product=7, qty=1, size=42}
   ↓ Redirige a /cart
   ↓ Contador en navbar: "+1" en el carrito

2️⃣ VER CARRITO
   Cliente navega a "/cart"
   ↓ CartController.view()
   ↓ cartService.getItems() → recupera sesión
   ↓ Muestra tabla: [Producto | Talla | Cantidad | Precio | Subtotal]
   ↓ Total al pie: suma de subtotales
   ↓ Botones: Modificar cantidad / Eliminar / Vaciar / Ir a checkout

3️⃣ CHECKOUT (COMPRA FINAL)
   Cliente hace clic en "Finalizar compra"
   ↓ GET /checkout
   ↓ CheckoutController.checkoutForm()
   ↓ Carga datos del usuario autenticado (mail, teléfono, etc.)
   ↓ Renderiza: templates/cart/checkout.html
   ↓ Muestra formulario con:
      - Items del carrito (resumen)
      - Datos del cliente (pre-rellenados)
      - Dirección de envío
      - Botón "Confirmar compra"

4️⃣ PROCESAR PEDIDO
   Cliente rellena datos y hace clic "Confirmar"
   ↓ POST /checkout
   ↓ CheckoutController.processCheckout()
   
   VALIDACIONES:
   ✓ ¿Carrito vacío? → Redirige a /cart
   ✓ ¿Email duplicado? → Refresca sesión de Spring Security
   
   PROCESAMIENTO:
   ✓ Actualiza User con nuevos datos (si cambió)
   ↓ Llama orderService.createFromCart()
   
      ├─ Genera orderNumber único (PED-2026-00001)
      ├─ Crea Order en BD
      ├─ Para cada CartItem:
      │  ├─ Crea OrderItem
      │  ├─ productService.decrementStock() ← VITAL: resta stock
      │  └─ Guarda detalles
      └─ Retorna Order creada
   
   ↓ cartService.clear() ← Vacía sesión
   ↓ Redirige a /orders/{orderID}?confirmed=true

5️⃣ CONFIRMACIÓN
   Cliente ve: "Pedido confirmado"
   ↓ GET /orders/{orderID}
   ↓ OrderController.detail()
   ↓ Renderiza: templates/user/order-detail.html
   ↓ Muestra:
      - Número de pedido
      - Estado: PENDIENTE
      - Fecha
      - Items comprados
      - Total
      - Botón descargar factura (PDF)

6️⃣ HISTORIAL
   Cliente navega a "/orders"
   ↓ OrderController.myOrders()
   ↓ Busca orderService.findByUser(userId)
   ↓ Muestra tabla con todos sus pedidos históricos

7️⃣ FACTURA
   Cliente hace clic en "Descargar factura"
   ↓ GET /orders/{id}/invoice
   ↓ OrderController.downloadInvoice()
   ↓ pdfService.generateInvoice(order)
   ↓ Descarga PDF con:
      - Logo SNIKERS
      - Factura-2026-{numero}
      - Datos cliente
      - Detalles productos
      - Total
      - Fecha
```

---

### **Flujo 3: Administración (solo ADMIN)**
```
1️⃣ ACCEDER AL ADMIN
   Admin entra en "/admin"
   ↓ SecurityConfig: ¿role = ADMINISTRADOR? ✅
   ↓ AdminController.dashboard()
   ↓ Carga métricas:
      - Total productos
      - Total pedidos
      - Total categorías
      - Últimos 5 pedidos
   ↓ Renderiza: templates/admin/dashboard.html

2️⃣ GESTIONAR PRODUCTOS
   Admin entra en "/admin/products"
   ↓ AdminController.products()
   ↓ productService.findAllActive()
   ↓ Muestra tabla con todos (incluso bajos de stock)

   CREAR NUEVO:
   ↓ Click en "Nuevo producto"
   ↓ GET /admin/products/new
   ↓ AdminController.newProductForm()
   ↓ Renderiza: templates/admin/product-form.html
   ↓ Campos: nombre, marca, precio, stock, categoría, foto, etc.
   ↓ POST /admin/products
   ↓ Valida @Valid
   ↓ Sube foto como Base64 si existe
   ↓ productService.save(product)
   ↓ Redirige a /admin/products

   EDITAR:
   ↓ Click en lápiz del producto
   ↓ GET /admin/products/{id}/edit
   ↓ adminController.editProductForm()
   ↓ productService.findById({id})
   ↓ Renderiza formulario pre-rellenado
   ↓ POST /admin/products
   ↓ productService.update({id}, updated)
   ↓ Redirige a /admin/products

   ELIMINAR:
   ↓ Click en X del producto
   ↓ POST /admin/products/{id}/delete
   ↓ productService.softDelete({id})
   ↓ Setea active = false (no se muestra, pero queda en BD)

3️⃣ GESTIONAR PEDIDOS
   Admin entra en "/admin/orders"
   ↓ AdminController.orders()
   ↓ orderService.findAll()
   ↓ Muestra tabla con estado de cada pedido

   CAMBIAR ESTADO:
   ↓ Selector dropdown: PENDIENTE → CONFIRMADO → ENVIADO
   ↓ POST /admin/orders/{id}/status?status=ENVIADO
   ↓ orderService.updateStatus({id}, ENVIADO)
   ↓ Redirige a /admin/orders
   ↓ El cliente ve el cambio cuando consulta /orders
```

---

## 💾 BASE DE DATOS (MySQL)

### **Esquema relacional**
```sql
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- Encriptada con BCrypt
    nombre_completo VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    foto_perfil MEDIUMTEXT,  -- Base64
    rol ENUM('CLIENTE','ADMINISTRADOR') DEFAULT 'CLIENTE',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categorias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(60) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE productos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    marca VARCHAR(60) NOT NULL,
    descripcion VARCHAR(2000),
    precio DECIMAL(9,2) NOT NULL,
    precio_original DECIMAL(9,2),
    cantidad_stock INT NOT NULL CHECK (cantidad_stock >= 0),
    url_imagen MEDIUMTEXT,  -- Base64
    color VARCHAR(30),
    rango_tallas VARCHAR(20),  -- "38-46"
    destacado BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    categoria_id BIGINT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    INDEX idx_product_name (nombre),
    INDEX idx_product_brand (marca)
);

CREATE TABLE pedidos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero_pedido VARCHAR(30) UNIQUE NOT NULL,  -- PED-2026-00001
    estado ENUM('PENDIENTE','CONFIRMADO','ENVIADO','ENTREGADO','CANCELADO') DEFAULT 'PENDIENTE',
    direccion_envio VARCHAR(255),
    cantidad_total DECIMAL(9,2),
    usuario_id BIGINT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE detalles_pedido (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cantidad INT NOT NULL,
    talla VARCHAR(5),
    precio_en_compra DECIMAL(9,2),
    subtotal DECIMAL(9,2),
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);
```

---

## 🎨 FRONTEND

### **Estructura de plantillas (Thymeleaf)**
```
templates/
├── home.html                   (Portada con slider)
├── fragments/
│  ├── layout.html              (Base HTML + navbar + footer)
│  └── ... (fragmentos reutilizables)
├── products/
│  ├── list.html                (Catálogo con paginación)
│  └── detail.html              (Detalle de zapatilla)
├── cart/
│  ├── view.html                (Resumen carrito)
│  └── checkout.html            (Formulario compra)
├── auth/
│  ├── login.html               (Iniciar sesión)
│  └── register.html            (Registro nuevo usuario)
├── user/
│  ├── orders.html              (Historial de pedidos)
│  └── order-detail.html        (Detalle de pedido)
├── admin/
│  ├── dashboard.html           (Panel principal)
│  ├── products.html            (CRUD productos)
│  ├── categories.html          (CRUD categorías)
│  ├── orders.html              (Gestión pedidos)
│  └── users.html               (Gestión usuarios)
└── error.html                  (Página de error unificada)

static/
├── css/
│  └── main.css                 (Estilos custom)
└── js/
   └── main.js                  (Interactividad)
```

---

## 🚀 CARACTERÍSTICAS ESPECIALES

### **1. Carrito persistente en sesión**
```
Cada usuario tiene su propio carrito mientras está logueado.
Si cierra sesión → carrito se limpia.
Si recarga página → carrito se mantiene.

Implementación: @Scope("session") en CartService
```

### **2. Base64 para fotos (sin almacenamiento externo)**
```
Usuario sube foto → convertida a Base64
"data:image/jpeg;base64,/9j/4AAQSkZJRgAB..." 
Se guarda directamente en BD (MEDIUMTEXT)
Se renderiza en <img src="...">
Ventaja: No necesita carpeta /uploads o cloud storage
```

### **3. Soft Delete (baja lógica)**
```
Al "eliminar" un producto → active = false
No se borra de BD (auditoria, histórico)
Se esconde en catálogo y búsquedas
Aún aparece en pedidos históricos
```

### **4. Stock automático**
```
Al crear Order:
  Para cada OrderItem:
    productService.decrementStock(productId, qty)
    
Valida: if (stock < qty) → throw IllegalStateException
Previene sobreventa
```

### **5. Generación de PDF**
```
PdfService.generateInvoice(Order order)
  ├─ Datos del cliente
  ├─ Items del pedido con precios
  ├─ Total
  ├─ Logo de tienda
  └─ Retorna byte[] → descarga navegador
```

### **6. Búsqueda full-text**
```
productRepository.search(query, pageable)
Busca en: nombre, marca, descripción
LIKE LOWER(CONCAT('%', query, '%'))
Insensible a mayúsculas
```

### **7. Paginación**
```
/products?page=0&size=12&sort=createdAt,desc
12 productos por página
Primer página: ?page=0
Segunda página: ?page=1
Ordenamiento: campo,dirección (asc/desc)
```

---

## 📊 DIAGRAMA DE FLUJO (Compra completa)

```
┌──────────┐
│  Cliente │
└────┬─────┘
     │
     ├─→ GET / ─────────────→ HomeController ──→ productService.findFeatured() ──→ BD
     │                            │
     │                            └─→ Renderiza: home.html
     │
     ├─→ GET /products ────→ ProductController ──→ productService.findAll(page) ──→ BD
     │                            │
     │                            └─→ Renderiza: products/list.html
     │
     ├─→ GET /products/5 ───→ ProductController ──→ productService.findById(5) ──→ BD
     │                            │
     │                            └─→ Renderiza: products/detail.html
     │
     ├─→ POST /cart/add ────→ CartController ──→ CartService ──→ SESIÓN (no BD)
     │    ?productId=5
     │    &quantity=1
     │
     ├─→ GET /cart ────────→ CartController ──→ CartService.getItems() ──→ SESIÓN
     │                            │
     │                            └─→ Renderiza: cart/view.html
     │
     ├─→ GET /checkout ────→ CheckoutController ──→ Renderiza: cart/checkout.html
     │                       (pre-rellena datos del User)
     │
     ├─→ POST /checkout ───→ CheckoutController ──→ orderService.createFromCart()
     │                                                    │
     │                                                    ├─→ OrderRepository.save()
     │                                                    │
     │                                                    ├─→ Para cada item:
     │                                                    │    ├─ OrderItemRepository.save()
     │                                                    │    └─ productService.decrementStock()
     │                                                    │       [REST 1 del stock]
     │                                                    │
     │                                                    └─→ CartService.clear()
     │
     └─→ GET /orders/123 ──→ OrderController ──→ orderService.findById(123)
         ?confirmed=true        │
                                └─→ Renderiza: user/order-detail.html
                                    [Muestra "Pedido confirmado"]
```

---

## 🔧 CONFIGURACIÓN IMPORTANTE

### **application.properties (Desarrollo)**
```properties
# Base de datos H2 (en memoria, para tests rápidos)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true

# Thymeleaf
spring.thymeleaf.cache=false  # Recarga plantillas sin reiniciar

# Encoding UTF-8
server.servlet.encoding.charset=UTF-8
```

### **application-prod.properties (MySQL real)**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/snikers_shop
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=validate  # No crea tablas automáticas
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

---

## 📝 RESUMEN EJECUTIVO

| Aspecto | Valor |
|--------|-------|
| **Controladores** | 8 (público + admin) |
| **Servicios** | 6 (lógica de negocio) |
| **Modelos** | 5 (User, Product, Category, Order, OrderItem) |
| **Repositorios** | 4 (Spring Data JPA) |
| **Tablas BD** | 5 |
| **Usuarios de prueba** | admin@snikers.shop / admin1234 |
| | cliente@snikers.shop / cliente123 |
| **Encriptación** | BCrypt (contraseñas) |
| **Almacenamiento imágenes** | Base64 en BD |
| **Facturas** | PDF generadas dinámicamente |
| **Stock** | Control automático (decremento en compra) |
| **Sesión** | HTTP Session (carrito, autenticación) |

---

## 🎓 CONCLUSIÓN

**SNIKERS SHOP** es una aplicación de e-commerce completa que demuestra:
- ✅ Arquitectura MVC limpia y separada
- ✅ Seguridad con Spring Security + BCrypt
- ✅ Base de datos relacional normalizada
- ✅ Validación en frontend y backend
- ✅ Gestión de transacciones (Orders)
- ✅ Control de stock
- ✅ Generación de reportes (PDF)
- ✅ Interfaz responsive y amigable

**Ideal para portafolio de desarrollador Full-Stack Java.**

---

_Última actualización: Abril 2026 — Proyecto Transversal DAM/DAW_
