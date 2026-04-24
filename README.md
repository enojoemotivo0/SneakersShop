# 👟 SNIKERS SHOP

> Tienda premium de sneakers construida con Spring Boot · Thymeleaf · MySQL

**Proyecto Transversal Final** · 2º DAM/DAW · Curso 2025-2026

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring](https://img.shields.io/badge/Spring_Boot-3.2-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📋 Descripción

**Snikers Shop** es un e-commerce monolítico especializado en sneakers premium, diseñado como simulación real de un entorno profesional. El proyecto integra los módulos del ciclo formativo:

- **Programación** — backend en Java + Spring Boot
- **Bases de Datos** — MySQL 8 con 5 tablas relacionadas
- **Lenguaje de Marcas + SEO** — Thymeleaf, Bootstrap 5 y meta tags
- **Sistemas Informáticos** — configuración de máquinas virtuales y bases de datos locales
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

## 🚀 Guía de Instalación y Uso

Dado que este proyecto no utiliza Docker, la base de datos MySQL debe estar instalada y ejecutándose de forma nativa en el sistema anfitrión.

### Opción A: Ejecución en Windows (Local o VM)

**Requisitos previos:**
- Java Development Kit (JDK) 17 o superior.
- Git instalado.
- Servidor MySQL (se recomienda usar **XAMPP** o **WAMP** para mayor facilidad).
- Maven (Opcional, se recomienda ejecutar desde el IDE como VS Code o Eclipse).

**Pasos:**
1. Inicia tu servidor MySQL (por ejemplo, arrancando el módulo MySQL en el panel de control de XAMPP/WAMP).
2. Entra a phpMyAdmin (`http://localhost/phpmyadmin`) o usa tu cliente SQL preferido y crea una base de datos vacía llamada `tienda`.
3. Clona el repositorio en tu ordenador:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd Sneaker-shop2
   ```
4. Abre la carpeta del proyecto en tu IDE (por ejemplo, Visual Studio Code).
5. Abre el archivo `SnikersShopApplication.java` y haz clic en el botón **Run** (o Start Debugging) que ofrece el IDE para iniciar la aplicación Spring Boot.
6. La aplicación conectará automáticamente con MySQL, creará las tablas necesarias en la base de datos `tienda` e insertará datos iniciales.
7. Abre tu navegador y accede a `http://localhost:8080`.

---

### Opción B: Ejecución en Máquina Virtual Linux (Ubuntu/Debian)

**Requisitos previos:**
Una máquina virtual Linux con una terminal de comandos y acceso a internet.

**Pasos:**

1. **Actualiza el sistema e instala las dependencias necesarias** (Git, Java 17, MySQL Server y Maven):
   ```bash
   sudo apt update
   sudo apt install git openjdk-17-jdk mysql-server maven -y
   ```

2. **Inicia y configura la base de datos MySQL**:
   Asegúrate de que el servicio esté corriendo:
   ```bash
   sudo systemctl start mysql
   sudo systemctl enable mysql
   ```
   Entra a la consola de MySQL:
   ```bash
   sudo mysql -u root
   ```
   Dentro de la consola de MySQL, crea la base de datos y configurala (ajusta el usuario/contraseña si tu `application-dev.properties` los requiere, por defecto la app usa usuario `root` sin contraseña en el entorno local, lo cual puedes cambiar según tu configuración):
   ```sql
   CREATE DATABASE tienda;
   -- Si necesitas ponerle contraseña al root para que coincida con tu properties:
   ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';
   FLUSH PRIVILEGES;
   EXIT;
   ```

3. **Clona el repositorio**:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd Sneaker-shop2
   ```

4. **Ejecuta la aplicación usando Maven**:
   ```bash
   mvn spring-boot:run
   ```

5. La aplicación descargará las librerías necesarias, compilará el código y levantará el servidor en el puerto 8080.
6. Accede a través del navegador de la máquina virtual (o desde tu máquina anfitriona si los puertos de la VM están ruteados) a: `http://localhost:8080` o `http://<IP_DE_LA_MAQUINA_VIRTUAL>:8080`.

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
├── pom.xml                           # dependencias Maven
└── README.md
```

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
