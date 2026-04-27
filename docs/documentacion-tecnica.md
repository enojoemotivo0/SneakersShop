# 📘 Documentación técnica · Snikers Shop

> Documento técnico del proyecto transversal final · 2º DAM/DAW · Curso 2025-2026

---

## 1. Descripción del proyecto

**Snikers Shop** es una aplicación web monolítica que simula una tienda online especializada en sneakers premium de cuatro categorías (Running, Basketball, Lifestyle y Skate). El proyecto representa una implementación profesional que integra los cinco módulos cursados durante el ciclo formativo.

### 1.1 Objetivos

- Simular un entorno e-commerce real con carrito, checkout, gestión de pedidos y panel de administración.
- Demostrar el dominio de **Spring Boot**, **JPA**, **Thymeleaf**, **Spring Security** y **MySQL**.
- Ofrecer una experiencia de usuario diferenciada del resto de clones de Nike que predominan en proyectos educativos, con énfasis en el **frontend premium** y un **hero slider animado**.
- Cumplir en su totalidad la rúbrica del proyecto (50 puntos posibles + 1 bonus).

### 1.2 Alcance funcional

| Área | Funcionalidades |
|------|-----------------|
| Catálogo público | Listado con filtros, búsqueda, paginación, ordenación, detalle de producto |
| Carrito | Añadir, actualizar cantidades, eliminar, vaciar (persistente en sesión) |
| Autenticación | Registro + login con contraseñas BCrypt, roles CUSTOMER/ADMIN |
| Checkout | Formulario de envío, creación de pedido, descuento de stock |
| Área personal | Historial de pedidos, detalle con estado |
| Administración | Dashboard, CRUD de productos y categorías, gestión de estados de pedidos |

---

## 2. Tecnologías utilizadas

### 2.1 Backend

- **Java 17** (LTS)
- **Spring Boot 3.2.5** — framework principal
  - `spring-boot-starter-web` — servidor embebido Tomcat + Spring MVC
  - `spring-boot-starter-data-jpa` — persistencia con Hibernate
  - `spring-boot-starter-security` — autenticación y autorización
  - `spring-boot-starter-thymeleaf` — motor de plantillas
  - `spring-boot-starter-validation` — Bean Validation
- **Hibernate 6** — implementación JPA
- **Lombok** — reducción de código boilerplate
- **Maven 3.9** — build y gestión de dependencias

### 2.2 Frontend

- **Thymeleaf 3** — motor de plantillas server-side
- **Bootstrap 5.3** — framework CSS (grid, componentes base)
- **CSS custom** — ~900 líneas de sistema de diseño propio
- **JavaScript vanilla** — slider, interacciones (sin frameworks JS)
- **Google Fonts** — Archivo Black, Inter Tight, JetBrains Mono

### 2.3 Base de datos

- **MySQL 8.0** (entorno local)
- **H2 Database** (desarrollo, en memoria)

### 2.4 Entorno de ejecución

- **Windows / Linux** con JDK 17+
- **Maven Wrapper** (`mvnw`, `mvnw.cmd`) para build reproducible
- **MySQL local** para el perfil `dev`

### 2.5 Herramientas

- **IntelliJ IDEA** / VS Code — IDE
- **Git** + **GitHub** — control de versiones
- **DBeaver** / phpMyAdmin — inspección de BD
- **Postman** — pruebas (opcional)

---

## 3. Arquitectura

### 3.1 Patrón MVC y separación en capas

La aplicación sigue estrictamente el patrón **Modelo-Vista-Controlador** con separación en tres capas anotadas mediante stereotypes de Spring:

```
┌──────────────────────────────────────────────────────┐
│  Cliente (navegador)                                 │
│       ↓ HTTP                                         │
├──────────────────────────────────────────────────────┤
│  @Controller         (capa de presentación)          │
│       ↓                                              │
│  @Service            (capa de lógica de negocio)     │
│       ↓                                              │
│  @Repository         (capa de persistencia / JPA)    │
│       ↓ SQL                                          │
├──────────────────────────────────────────────────────┤
│  MySQL 8.0                                           │
└──────────────────────────────────────────────────────┘
```

### 3.2 Responsabilidades por capa

**Controladores** — reciben la petición HTTP, validan la entrada, delegan en servicios y devuelven la vista. Nunca acceden directamente a repositorios ni contienen lógica de negocio compleja.

**Servicios** — contienen toda la lógica de negocio. Orquestan llamadas a uno o varios repositorios. Las operaciones que alteran la BD se marcan con `@Transactional` para garantizar atomicidad.

**Repositorios** — interfaces que extienden `JpaRepository<T, ID>`. Spring Data genera la implementación en tiempo de arranque. Los casos especiales usan `@Query` con JPQL.

### 3.3 Inyección de dependencias

Se usa **inyección por constructor** (recomendada) mediante Lombok `@RequiredArgsConstructor`. Esto:

- Permite marcar campos como `final` (inmutables).
- Facilita el testing (no necesita reflexión).
- Detecta dependencias circulares en tiempo de compilación.

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    // Spring inyecta el repositorio automáticamente
}
```

### 3.4 Seguridad

Spring Security 6 protege la aplicación con:

- **Autenticación** mediante formulario HTML (sesión HTTP + cookie).
- **Autorización** declarativa en `SecurityConfig`:
  - `/admin/**` → requiere rol ADMIN
  - `/checkout/**`, `/orders/**`, `/profile/**` → autenticado
  - Resto → público
- **Hashing** con **BCrypt** (10 rondas por defecto).
- **UserDetailsService** personalizado (`UserService`) que carga el usuario desde la BD.
- CSRF deshabilitado en este proyecto académico por simplicidad (en producción se activaría).

### 3.5 Gestión de errores

- `GlobalExceptionHandler` con `@ControllerAdvice` captura excepciones y renderiza una página `error.html` unificada.
- Errores comunes manejados:
  - `IllegalArgumentException` → 404
  - `IllegalStateException` → 400 (ej: stock insuficiente)
  - Excepción genérica → 500

---

## 4. Base de datos

### 4.1 Modelo físico

5 tablas relacionadas con claves foráneas:

| Tabla | Descripción | Registros demo |
|-------|-------------|----------------|
| categories | Categorías de sneakers | 4 |
| products | Sneakers individuales | 8 |
| users | Clientes y admins | 2 |
| orders | Pedidos realizados | 0 (generados en uso) |
| order_items | Líneas de pedido | 0 |

### 4.2 Relaciones

- `categories` 1:N `products`
- `users` 1:N `orders`
- `orders` 1:N `order_items`
- `products` 1:N `order_items`

Ver el diagrama completo en [`diagrama-er.md`](diagrama-er.md).

### 4.3 Integridad referencial

- `products.category_id → categories.id` con `ON DELETE CASCADE` (al borrar una categoría se borran sus productos).
- `orders.user_id → users.id` con `ON DELETE CASCADE`.
- `order_items.order_id → orders.id` con `ON DELETE CASCADE`.
- `order_items.product_id → products.id` con `ON DELETE RESTRICT` (no se permite borrar un producto que figure en pedidos — se usa soft-delete con la columna `active`).

### 4.4 Estrategia de DDL

En el entorno local, `spring.jpa.hibernate.ddl-auto=update` deja que Hibernate actualice el esquema, pero `schema.sql` está disponible como referencia canónica y para despliegues manuales.

En `dev` (H2), se usa `create-drop` para arrancar siempre limpio.

### 4.5 Datos iniciales

`DataInitializer.java` (clase `@Component` que implementa `CommandLineRunner`) inserta automáticamente en el primer arranque:

- 4 categorías
- 8 productos con imágenes reales de Unsplash
- 2 usuarios (admin y cliente) con contraseñas BCrypt

---

## 5. Frontend y SEO

### 5.1 Sistema de diseño

Se diseñó un sistema propio en `main.css` con variables CSS para mantener coherencia:

```css
:root {
  --snk-black: #0a0a0a;
  --snk-volt: #d6ff3e;      /* acento neón */
  --snk-cream: #f4f1ea;
  --font-display: 'Archivo Black', sans-serif;
  --font-body: 'Inter Tight', sans-serif;
  /* ... */
}
```

Paleta monocromo + acento neón "volt", inspirada en pistas de atletismo profesionales. Evita deliberadamente la estética "AI genérica" (Inter + gradientes lila).

### 5.2 Hero slider

El slider del home es el elemento diferenciador del proyecto:

- **4 slides** con imágenes a pantalla completa
- **Auto-play** de 6 segundos por slide con barra de progreso visible
- **Animaciones escalonadas de entrada** (CSS transitions con delays):
  1. Tag (0.3s)
  2. Título grande (0.5s)
  3. Subtítulo (0.7s)
  4. Botones (0.9s)
- **Efecto Ken Burns** (zoom lento del fondo durante 8 segundos)
- **Controles múltiples**:
  - Flechas (← →)
  - Puntos indicadores con barra de progreso
  - Teclado (flechas izquierda/derecha)
  - Swipe táctil en móvil
- **Pausa automática** al hacer hover o cuando la pestaña pierde el foco
- **Ticker promocional** en la parte inferior con scroll infinito

Implementado en JavaScript vanilla (~150 líneas) para no depender de librerías externas.

### 5.3 HTML semántico

Todas las páginas usan la estructura semántica recomendada:

```html
<header> ... </header>
<main>
  <section>
    <article>
      <h1>...</h1>
      <h2>...</h2>
    </article>
  </section>
</main>
<footer> ... </footer>
```

### 5.4 SEO

- `<title>` único y descriptivo por cada página (pasado desde el controlador)
- `<meta description>` por página con resumen
- `<meta name="keywords">` con palabras clave del nicho
- **Open Graph** (`og:title`, `og:description`, `og:type`, `og:site_name`)
- **Twitter Card** (`summary_large_image`)
- Jerarquía correcta de encabezados: un único `<h1>` por página, `<h2>` para secciones, `<h3>` para subsecciones
- URLs semánticas y legibles

### 5.5 Diseño responsive

- Breakpoints principales: 992px (tablet) y 768px (móvil)
- Grid responsive con `repeat(auto-fill, minmax(280px, 1fr))`
- Categorías asimétricas en desktop que se simplifican en móvil
- Slider adaptativo (puntos más pequeños y controles apilados en móvil)

---

## 6. Instrucciones de despliegue local

### 6.1 Requisitos previos

- Git
- JDK 17 o superior
- MySQL local levantado
- 2 GB de RAM libres

### 6.2 Pasos desde clone

```bash
git clone https://github.com/<tu-usuario>/snikers-shop.git
cd snikers-shop
```

Crear base de datos local:

```sql
CREATE DATABASE tienda;
```

Arranque en Windows (recomendado):

```powershell
.\iniciar-app.ps1
```

Arranque manual alternativo:

```bash
./mvnw spring-boot:run
```

### 6.3 Verificación

Si todo va bien, en logs aparecerá el inicio de Spring Boot y la aplicación quedará en http://localhost:8080.

---

## 7. Estrategia de testing

El proyecto incluye tests básicos de integración. Para ejecutarlos:

```bash
./mvnw test
```

Se ha priorizado testing manual exhaustivo durante la demo dada la naturaleza del proyecto académico.

---

## 8. Cumplimiento de la rúbrica

| Apartado | Requisito | Dónde se implementa |
|----------|-----------|---------------------|
| **Programación** (10 pt) | | |
| MVC | Separación estricta de capas | `controller/`, `service/`, `repository/` |
| CRUD | Productos y categorías con CRUD completo | `ProductService`, `AdminController` |
| Spring Boot | Uso correcto del framework | Todo el backend |
| Calidad del código | Lombok, inyección por constructor, `@Transactional` | — |
| Validaciones | Bean Validation en todas las entidades | `@NotBlank`, `@Email`, `@Min`, etc. |
| **Base de Datos** (10 pt) | | |
| Modelo ER | En Mermaid | `docs/diagrama-er.md` |
| Modelo relacional | Implementado en MySQL | `sql/schema.sql` |
| Relaciones correctas | 4 relaciones 1:N con FK | `schema.sql` |
| Script SQL | Creación + datos | `sql/schema.sql`, `sql/data.sql` |
| Integración con la app | JPA + `application-prod.properties` | Configuración MySQL |
| **Marcas + SEO** (10 pt) | | |
| HTML semántico | header/main/section/article/footer | Todas las plantillas |
| Responsive | Media queries + Bootstrap | `main.css` |
| SEO básico | title, meta description, jerarquía h1-h6, Open Graph | `fragments/layout.html` |
| Formularios funcionales | Login, registro, checkout, admin | `auth/`, `cart/`, `admin/` |
| Usabilidad | Diseño cuidado, animaciones, feedback visual | Hero slider, cards con hover, badges |
| **Sistemas** (10 pt) | | |
| Entorno Linux | Ubuntu con JDK 17 + MySQL | `README.md` |
| Ejecución local | Maven Wrapper + perfil dev | `mvnw`, `mvnw.cmd` |
| Base de datos | MySQL local + scripts SQL | `sql/schema.sql`, `sql/data.sql` |
| **Entornos** (10 pt) | | |
| Repositorio GitHub | — | README.md |
| Commits de calidad | Al menos 5, mensajes profesionales | Ver historial |
| Diagrama de clases | UML en Mermaid | `docs/diagrama-clases.md` |
| Documentación | Completa | Este documento |
| Uso de Git | Ramas, commits atómicos | Historial |
| **Bonus** (+1 pt) | | |
| Diagrama de secuencia | Flujo de compra | `docs/diagrama-clases.md` |
| Funcionalidades extra | Hero slider animado, soft-delete, roles de usuario, phpMyAdmin, paginación, búsqueda, descuentos | — |

---

## 9. Decisiones técnicas destacadas

### ¿Por qué Spring Boot en lugar de otro framework?

- Ecosistema maduro y muy demandado profesionalmente.
- Autoconfiguración reduce boilerplate.
- Integra de forma natural con JPA, Security, Validation y Thymeleaf.

### ¿Por qué Thymeleaf en lugar de una SPA (React/Vue)?

- El proyecto exige arquitectura **monolítica**.
- Thymeleaf permite renderizado server-side optimizado para SEO (los crawlers ven HTML completo, no JavaScript por renderizar).
- Menos infraestructura (no hace falta un segundo proyecto frontend).

### ¿Por qué carrito en sesión y no en BD?

- Simplicidad: el carrito es volátil por naturaleza.
- Funciona también para usuarios anónimos (antes de hacer login).
- Menos escritura en BD.
- Se materializa en BD solo cuando el pedido se confirma.

### ¿Por qué soft-delete en productos?

- Los productos pueden estar referenciados en pedidos antiguos.
- Eliminar físicamente rompería la integridad histórica.
- `active = false` oculta del catálogo pero conserva la trazabilidad.

### ¿Por qué Maven Wrapper para ejecución?

- Evita depender de una versión global concreta de Maven.
- Reduce errores de entorno en la exposición.
- Permite un arranque más consistente entre distintos ordenadores.

---

## 10. Posibles mejoras futuras

- Integración con pasarela de pago real (Stripe, Redsys)
- Subida de imágenes propias (actualmente se usan URLs de Unsplash)
- Sistema de reseñas y valoraciones
- Wishlist de productos favoritos
- Envío de emails transaccionales (confirmación de pedido)
- API REST adicional para una app móvil futura
- Tests automatizados (JUnit + Mockito) con buena cobertura
- CI/CD con GitHub Actions
- Cache con Redis
- Paginación del lado del servidor en listados largos del admin

---

## 11. Conclusiones

El proyecto integra los cinco módulos del ciclo de forma coherente y profesional, cumpliendo la totalidad de la rúbrica:

- La **arquitectura MVC** con separación en capas permite escalabilidad y testabilidad.
- La **base de datos relacional** respeta las formas normales y la integridad referencial.
- El **frontend** se diferencia deliberadamente de las tiendas online genéricas, con un hero slider animado y un sistema de diseño editorial propio.
- **Maven Wrapper + configuración documentada** garantiza un despliegue reproducible en cualquier máquina con Java y MySQL.
- El **repositorio Git** muestra una evolución incremental del proyecto.

Todo el código está pensado para ser **defendible oralmente**: cada decisión técnica tiene una justificación clara y documentada aquí.

---

**Fin del documento** · 2025-2026 · Proyecto Transversal Final DAM/DAW
