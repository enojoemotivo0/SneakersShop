# 👟 SNIKERS SHOP

> Tienda premium de sneakers construida con Spring Boot · Thymeleaf · MySQL · Docker

**Proyecto Transversal Final** · 2º DAM/DAW · Curso 2025-2026

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring](https://img.shields.io/badge/Spring_Boot-3.2-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Descripción

**Snikers Shop** es un e-commerce monolítico especializado en sneakers premium, diseñado como simulación real de un entorno profesional. El proyecto integra los 5 módulos del ciclo formativo:

- **Programación** — backend en Java + Spring Boot
- **Bases de Datos** — MySQL 8 con 5 tablas relacionadas
- **Lenguaje de Marcas + SEO** — Thymeleaf, Bootstrap 5 y meta tags
- **Sistemas Informáticos** — despliegue con Docker Compose
- **Entornos de Desarrollo** — Git, versionado, documentación

---

## 🚀 Tecnologías

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Persistencia | Spring Data JPA + Hibernate |
| Seguridad | Spring Security 6 (BCrypt) |
| Motor de plantillas | Thymeleaf |
| Framework CSS | Bootstrap 5.3 + CSS propio |
| Base de datos | MySQL 8.0 (producción) / H2 (dev) |
| Build | Maven 3.9 |
| Contenedores | Docker + Docker Compose |
| Validación | Bean Validation (Jakarta) |
| Utilidades | Lombok |

---

## 🏗️ Arquitectura

Aplicación **monolítica** con patrón **MVC** y separación en capas:

```
┌─────────────────────────────────────────┐
│   Capa de presentación (@Controller)    │  ← Thymeleaf + Bootstrap
├─────────────────────────────────────────┤
│   Capa de servicio (@Service)           │  ← Lógica de negocio
├─────────────────────────────────────────┤
│   Capa de persistencia (@Repository)    │  ← Spring Data JPA
├─────────────────────────────────────────┤
│   Base de datos MySQL                   │
└─────────────────────────────────────────┘
```

### Entidades del dominio (5)
- **Category** — categoría de sneakers
- **Product** — sneaker con stock, precio, descuento
- **User** — cliente o admin (con Spring Security)
- **Order** — pedido realizado
- **OrderItem** — línea de pedido

Ver los diagramas completos en [`docs/diagrama-er.md`](docs/diagrama-er.md) y [`docs/diagrama-clases.md`](docs/diagrama-clases.md).

---

## ⚙️ Instalación y despliegue

### Opción A: con Docker (recomendada)

Requiere tener instalado **Docker** y **Docker Compose**.

```bash
# 1. Clonar el repositorio
git clone https://github.com/<tu-usuario>/snikers-shop.git
cd snikers-shop

# 2. Levantar todo el stack (app + MySQL + phpMyAdmin)
docker-compose up --build -d

# 3. Verificar que los contenedores están sanos
docker-compose ps
```

Acceso a los servicios:

| Servicio | URL | Notas |
|----------|-----|-------|
| 🛒 Tienda | http://localhost:8080 | Aplicación principal |
| 🔐 Admin | http://localhost:8080/admin | Login con admin@snikers.shop |
| 🗄️ phpMyAdmin | http://localhost:8081 | Inspección de la BD |
| 🐬 MySQL | localhost:3307 | Conexión directa (expuesto en 3307 para no chocar con MySQL nativo) |

Para detener todo:

```bash
docker-compose down           # conserva los datos
docker-compose down -v        # borra también el volumen de la BD
```

### Opción B: local sin Docker (perfil dev con H2)

```bash
./mvnw spring-boot:run
# o en Windows:
mvnw.cmd spring-boot:run
```

La aplicación arranca en http://localhost:8080 usando una BD H2 en memoria. Ideal para pruebas rápidas.

---

## 👤 Usuarios de prueba

El `DataInitializer` crea automáticamente dos usuarios al primer arranque:

| Rol | Email | Contraseña |
|-----|-------|-----------|
| 🛠️ Admin | admin@snikers.shop | admin1234 |
| 👤 Cliente | cliente@snikers.shop | cliente123 |

---

## 🎯 Funcionalidades

### Público
- Catálogo con filtros por categoría, búsqueda y ordenación
- Detalle de producto con selector de tallas
- Carrito en sesión (funciona sin login)
- Registro y login con Spring Security

### Cliente registrado
- Checkout con dirección de envío
- Historial de pedidos
- Detalle de cada pedido con estado

### Administrador
- Dashboard con métricas
- CRUD completo de productos
- CRUD de categorías
- Gestión de estados de pedidos (PENDING → PAID → SHIPPED → DELIVERED / CANCELLED)

---

## 🎨 Frontend

Diseño propio inspirado en editoriales de moda deportiva, pensado para diferenciarse de los clones genéricos:

- Tipografía **Archivo Black** + **Inter Tight** + **JetBrains Mono**
- Paleta monocromo (negro/crema) + acento neón "volt"
- **Hero slider automático** de 4 slides con:
  - Auto-play cada 6 segundos + barra de progreso
  - Animaciones de entrada escalonadas (tag → título → subtítulo → botones)
  - Navegación por flechas, puntos, teclado (← →) y swipe táctil
  - Pausa al hacer hover
  - Zoom lento de fondo (efecto Ken Burns)
  - Ticker promocional con scroll infinito
- Tarjetas de producto con hover (rotación + zoom)
- HTML semántico (`<header>`, `<main>`, `<section>`, `<article>`, `<footer>`)
- Responsive (móvil, tablet, escritorio)

### SEO
- Etiquetas `<title>` y `<meta description>` dinámicas por página
- Open Graph + Twitter Card
- Jerarquía correcta de encabezados (h1 único por página, h2-h3 para secciones)
- URLs semánticas (`/products`, `/products/{id}`, `/cart`, etc.)

---

## 📂 Estructura del proyecto

```
snikers-shop/
├── src/main/java/com/snikers/shop/
│   ├── SnikersShopApplication.java   # punto de entrada
│   ├── controller/                   # capa de presentación
│   ├── service/                      # lógica de negocio
│   ├── repository/                   # acceso a datos
│   ├── model/                        # entidades JPA
│   ├── dto/                          # DTOs (CartItem)
│   └── config/                       # SecurityConfig, DataInitializer
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties    # perfil H2
│   ├── application-prod.properties   # perfil MySQL
│   ├── templates/                    # plantillas Thymeleaf
│   │   ├── home.html                 # home con slider
│   │   ├── products/                 # listado y detalle
│   │   ├── cart/                     # carrito y checkout
│   │   ├── auth/                     # login, register
│   │   ├── user/                     # pedidos del cliente
│   │   ├── admin/                    # panel administrativo
│   │   └── fragments/                # navbar, footer, head
│   └── static/
│       ├── css/main.css              # sistema de diseño (~900 líneas)
│       └── js/main.js                # slider + interacciones
├── sql/
│   ├── schema.sql                    # creación de tablas
│   └── data.sql                      # datos iniciales
├── docs/
│   ├── diagrama-er.md                # modelo entidad-relación
│   ├── diagrama-clases.md            # UML de clases
│   └── documentacion-tecnica.md      # documentación completa
├── Dockerfile                        # imagen multi-stage
├── docker-compose.yml                # app + MySQL + phpMyAdmin
├── pom.xml                           # dependencias Maven
└── README.md
```

---

## 🐳 Docker: detalles técnicos

`docker-compose.yml` define 3 servicios en una red aislada (`snikers-net`):

1. **db** — MySQL 8.0
   - Volumen persistente `snikers-db-data`
   - Scripts de inicialización (`schema.sql`, `data.sql`)
   - Healthcheck con `mysqladmin ping`

2. **app** — Spring Boot
   - Build multi-stage (JDK para compilar, JRE Alpine para ejecutar)
   - Usuario no-root dentro del contenedor
   - `depends_on` con `condition: service_healthy` para esperar a la BD
   - Healthcheck HTTP contra el endpoint raíz

3. **phpmyadmin** — Interfaz web de MySQL
   - Accesible en http://localhost:8081
   - Útil para debugging durante la defensa

---

## 📜 Scripts SQL

`sql/schema.sql` crea las 5 tablas con:
- Claves primarias auto-incrementales
- Claves foráneas con `ON DELETE CASCADE` / `RESTRICT` según el caso
- Restricciones `CHECK` para estados y valores mínimos
- Índices en columnas de búsqueda frecuente

---

## 🧪 Testing

Los tests básicos se encuentran en `src/test/java/com/snikers/shop`:

```bash
./mvnw test
```

---

## 📖 Documentación adicional

- [Diagrama Entidad-Relación](docs/diagrama-er.md)
- [Diagrama de clases](docs/diagrama-clases.md)
- [Documentación técnica completa](docs/documentacion-tecnica.md)

---

## 👨‍💻 Autor

**Thomas Rondo Chele Besopo Chele**
Proyecto Transversal Final — 2º DAM/DAW
Curso 2025-2026

---

## 📄 Licencia

Proyecto académico con fines educativos.
