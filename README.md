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

## 🚀 Guía de Instalación y Despliegue

Dado que este proyecto **no utiliza contenedores**, depende de que tengas los servicios instalados nativamente en tu sistema. A continuación, se detallan dos métodos completos de ejecución, dependiendo del entorno que vayas a usar.

### 🪟 Opción A: Ejecución en Windows (Entorno Local o Máquina Virtual)

Este es el proceso recomendado para desarrollar o ejecutar el proyecto en un ordenador con Windows, apoyándose en herramientas visuales.

**0. Requisitos indispensables:**
- **JDK 17** (Java Development Kit) instalado y configurado en el `PATH` del sistema.
- **Git** instalado para clonar el código.
- **XAMPP** o **WAMP Server** instalados (nos proporcionarán MySQL y phpMyAdmin de forma sencilla).
- Un IDE como **Visual Studio Code** o **IntelliJ IDEA**.

**Pasos de ejecución:**

1. **Levantar el servidor web y la Base de Datos:**
   * Abre el Panel de Control de XAMPP (o WAMP).
   * Haz clic en **"Start"** en los módulos de **Apache** y **MySQL**.
2. **Crear la base de datos de la tienda:**
   * Abre tu navegador y dirígete a `http://localhost/phpmyadmin`.
   * En el menú lateral izquierdo, pulsa en "Nueva".
   * Escribe el nombre de la base de datos: **`tienda`** (respeta las minúsculas).
   * No necesitas crear tablas ni importar archivos `.sql` manuales, Spring Boot se encargará de ello gracias a su configuración de *Hibernate*.
3. **Obtener el código fuente:**
   * Abre una terminal (PowerShell o CMD) en la carpeta donde quieras guardar el proyecto.
   * Ejecuta: 
     ```bash
     git clone https://github.com/enojoemotivo0/SneakersShop.git
     cd Sneaker-shop2
     ```
4. **Configurar credenciales (Opcional):**
   * Abre el proyecto en tu IDE (VS Code).
   * Revisa el archivo `src/main/resources/application-dev.properties`.
   * Por defecto, está configurado para conectarse al usuario `root` de MySQL sin contraseña (`spring.datasource.password=`), que es el estándar de XAMPP. Si tu base de datos tiene contraseña, escríbela ahí.
5. **Ejecutar la aplicación:**
   * Si usas VS Code: Abre el archivo `SnikersShopApplication.java` y pulsa "Run" encima de la función `main`.
   * Alternativa por consola: Ejecuta el comando `mvn spring-boot:run` o usa el wrapper `./mvnw spring-boot:run`.
6. **¡Listo!** Abre el navegador y visita: `http://localhost:8080`

---

### 🐧 Opción B: Ejecución en Linux (Máquina Virtual Ubuntu / Debian)

Este entorno simula un despliegue más parecido a producción. Operarás por 100% por comandos.

**Pasos de ejecución:**

1. **Actualizar repositorios del sistema:**
   ```bash
   sudo apt update && sudo apt upgrade -y
   ```
2. **Instalar los paquetes necesarios:**
   Instalaremos Git, el motor de Java 17, el servidor de bases de datos MySQL y la herramienta Maven de compilación.
   ```bash
   sudo apt install git openjdk-17-jdk mysql-server maven -y
   ```
3. **Gestión y securización de la Base de Datos MySQL:**
   * Comprueba que MySQL está corriendo: `sudo systemctl status mysql` (Debería salir activo/verde).
   * Abre la terminal interactiva de MySQL:
     ```bash
     sudo mysql -u root
     ```
   * Una vez dentro del prompt `mysql>`, lanza estos comandos SQL para preparar la BD y el usuario:
     ```sql
     -- Crear la base de datos
     CREATE DATABASE tienda;
     
     -- Crear un usuario específico y otorgarle permisos totales sobre la base de datos
     CREATE USER 'root'@'localhost' IDENTIFIED BY '';
     GRANT ALL PRIVILEGES ON tienda.* TO 'root'@'localhost';
     FLUSH PRIVILEGES;

     -- Salir de la terminal MySQL
     EXIT;
     ```
   * *Nótese que por motivos de compatibilidad con el properties por defecto se usó la password vacía. En un caso real se usa contraseña segura.*
4. **Clonar el proyecto:**
   ```bash
   git clone https://github.com/enojoemotivo0/SneakersShop.git
   cd Sneaker-shop2
   ```
5. **Compilar y ejecutar la aplicación:**
   * Al ejecutar este comando, Maven descargará automáticamente las dependencias de Spring Boot, compilará el código `.java` e iniciará el servidor embebido Tomcat.
   ```bash
   mvn spring-boot:run
   ```
6. **Verificar el servicio:**
   * Si estás trabajando dentro de una interfaz gráfica de Ubuntu, abre Firefox y entra a `http://localhost:8080`.
   * Si es una Máquina Virtual en modo "Bridge" o con reenvío de puertos desde puente, utiliza la IP de la máquina: `http://<IP_MAQUINA_LINUX>:8080` desde tu ordenador anfitrión.

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
