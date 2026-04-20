# 🧾 Guía Git — Snikers Shop

Secuencia de commits profesionales para cumplir con el requisito mínimo de 5 commits de la rúbrica. Cada commit representa una fase lógica del desarrollo.

## ⚡ Inicialización

```bash
cd snikers-shop
git init
git branch -M main
git config user.name "Tu Nombre"
git config user.email "tu@email.com"
```

---

## 📚 Secuencia de commits (6 commits sugeridos)

### Commit 1 · Estructura inicial del proyecto

```bash
# Añade el esqueleto del proyecto
git add pom.xml .gitignore .dockerignore README.md
git add src/main/java/com/snikers/shop/SnikersShopApplication.java
git add src/main/resources/application*.properties
git commit -m "chore: inicialización del proyecto Spring Boot con Maven

- Configuración del pom.xml con dependencias (Spring Boot 3.2, JPA, Security, Thymeleaf, MySQL)
- application.properties con perfiles dev (H2) y prod (MySQL)
- Clase principal SnikersShopApplication
- Archivos de configuración de Git y Docker"
```

### Commit 2 · Modelo de dominio y persistencia

```bash
git add src/main/java/com/snikers/shop/model/
git add src/main/java/com/snikers/shop/repository/
git add src/main/java/com/snikers/shop/dto/
git add sql/
git add docs/diagrama-er.md
git commit -m "feat(data): entidades JPA, repositorios y modelo de datos

- 5 entidades: Category, Product, User, Order, OrderItem
- Relaciones 1:N con claves foráneas (categories→products, users→orders, orders→order_items)
- Validaciones Bean Validation (@NotBlank, @Email, @Min)
- 4 repositorios con Spring Data JPA + query personalizada de búsqueda
- Script schema.sql y data.sql para MySQL
- Diagrama entidad-relación en Mermaid"
```

### Commit 3 · Capa de servicio y seguridad

```bash
git add src/main/java/com/snikers/shop/service/
git add src/main/java/com/snikers/shop/config/
git commit -m "feat(core): servicios de negocio y Spring Security

- ProductService con CRUD completo y gestión de stock
- CategoryService, UserService (UserDetailsService), OrderService
- CartService con scope de sesión para carrito persistente
- SecurityConfig con BCrypt, rutas públicas/privadas y roles ADMIN/CUSTOMER
- DataInitializer con seed de 4 categorías, 8 productos y 2 usuarios demo"
```

### Commit 4 · Controladores y flujos MVC

```bash
git add src/main/java/com/snikers/shop/controller/
git commit -m "feat(web): controladores MVC y flujos de la aplicación

- HomeController (página principal con productos destacados)
- ProductController (catálogo, detalle, búsqueda, paginación)
- CartController + CheckoutController (carrito y compra)
- AuthController (login, registro)
- OrderController (historial personal)
- AdminController (dashboard, CRUD productos/categorías, gestión de pedidos)
- GlobalExceptionHandler con página de error unificada"
```

### Commit 5 · Frontend premium y plantillas Thymeleaf

```bash
git add src/main/resources/templates/
git add src/main/resources/static/
git commit -m "feat(ui): frontend premium con hero slider animado

- Sistema de diseño custom en CSS (~900 líneas) con tipografía Archivo Black + Inter Tight
- Hero slider automático con 4 slides, auto-play, barra de progreso y animaciones escalonadas
- Navegación por flechas, puntos, teclado y swipe táctil
- Ticker promocional con scroll infinito
- Plantillas Thymeleaf para home, catálogo, detalle, carrito, checkout, auth, admin y área de cliente
- HTML semántico, SEO (title, meta description, Open Graph, Twitter Card)
- Diseño responsive con breakpoints 768px y 992px"
```

### Commit 6 · Docker + documentación técnica

```bash
git add Dockerfile docker-compose.yml
git add docs/
git commit -m "chore(deploy): contenerización con Docker y documentación completa

- Dockerfile multi-stage (build con JDK + runtime con JRE Alpine) con usuario no-root
- docker-compose.yml con 3 servicios: app, MySQL 8 y phpMyAdmin
- Red aislada snikers-net y volumen persistente para la BD
- Healthchecks en app y db
- Diagrama de clases UML en Mermaid con explicación por capas
- Diagrama de secuencia del flujo de compra
- Documentación técnica completa (arquitectura, despliegue, decisiones técnicas)"
```

---

## 🚀 Publicar en GitHub

```bash
# 1. Crea un repo vacío en github.com con el nombre "snikers-shop"
#    (NO lo inicialices con README, .gitignore ni licencia — ya los tienes en local)

# 2. Enlaza y sube
git remote add origin https://github.com/TU-USUARIO/snikers-shop.git
git push -u origin main
```

---

## ✅ Comprobar que todo funciona (Docker)

```bash
# Construir y arrancar
docker-compose up --build -d

# Verificar que los 3 contenedores están "healthy"
docker-compose ps

# Ver logs en vivo
docker-compose logs -f app
```

Una vez veas en los logs:

```
✅ Datos iniciales cargados.
Started SnikersShopApplication in ~X seconds
```

La tienda estará lista en:

- 🛒 **Tienda**: http://localhost:8080
- 🛠️ **Admin**: http://localhost:8080/admin · admin@snikers.shop / admin1234
- 🗄️ **phpMyAdmin**: http://localhost:8081
- 👤 **Cliente**: cliente@snikers.shop / cliente123

---

## 🧪 Script de prueba rápida (humo)

Para demostrar en la defensa que todo el flujo funciona:

1. Entra en http://localhost:8080 → ve el slider animado
2. Navega al catálogo → filtra por categoría Running
3. Abre un producto → selecciona talla 42, añade al carrito
4. Registra una nueva cuenta en /register
5. Completa el checkout
6. Consulta tu pedido en /orders
7. Cierra sesión y entra como admin → cambia el estado del pedido a SHIPPED
8. En phpMyAdmin comprueba que los datos están en MySQL

---

## 💡 Tips para la defensa oral

1. **Empieza por la arquitectura**: muestra el diagrama de clases y explica las 3 capas.
2. **Haz la demo con el slider**: es el elemento diferenciador del proyecto.
3. **Abre un terminal con docker-compose ps**: demuestra que los 3 contenedores están sanos.
4. **Enseña phpMyAdmin**: prueba que la BD está relacionada correctamente.
5. **Enseña el código fuente de una clase por capa** (controller → service → repository) para justificar la separación MVC.
6. **Prepárate preguntas**: "¿por qué soft-delete?", "¿por qué multi-stage Dockerfile?", "¿cómo funciona BCrypt?". Tienes respuestas en `docs/documentacion-tecnica.md`.

¡Mucha suerte! 👟
