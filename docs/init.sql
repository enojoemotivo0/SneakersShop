-- ============================================================
-- SNIKERS SHOP — init.sql
-- Creación de base de datos, usuario de aplicación y tablas
-- Motor: MySQL 8.0 · Columnas en español (igual que Hibernate)
-- Autor: Proyecto Transversal Final · 2º DAM/DAW
-- Ejecutar ANTES de data.sql  (requiere acceso como root)
-- ============================================================

CREATE DATABASE IF NOT EXISTS tienda
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- Usuario dedicado para la aplicación
-- Se usa en lugar de root para limitar el daño en caso de ataque
-- ============================================================
CREATE USER IF NOT EXISTS 'snikers_user'@'localhost' IDENTIFIED BY 'Snikers2025!';

-- Solo permisos sobre la base de datos del proyecto
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES
    ON tienda.*
    TO 'snikers_user'@'localhost';

FLUSH PRIVILEGES;

USE tienda;

-- Limpieza previa (orden inverso por claves foráneas)
DROP TABLE IF EXISTS detalles_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS categorias;

-- ============================================================
-- Tabla: categorias
-- ============================================================
CREATE TABLE categorias (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(50)  NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    url_amigable VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: usuarios
-- ============================================================
CREATE TABLE usuarios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(80)  NOT NULL,
    correo          VARCHAR(120) NOT NULL UNIQUE,
    contrasena      VARCHAR(120) NOT NULL,
    telefono        VARCHAR(20),
    direccion       VARCHAR(255),
    foto_perfil     MEDIUMTEXT,
    rol             VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE',
    fecha_creacion  DATETIME     NOT NULL,
    CONSTRAINT chk_rol CHECK (rol IN ('CLIENTE','ADMINISTRADOR'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: productos
-- Relación N:1 con categorias
-- ============================================================
CREATE TABLE productos (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(120)  NOT NULL,
    marca               VARCHAR(60)   NOT NULL,
    descripcion         VARCHAR(2000),
    precio              DECIMAL(9,2)  NOT NULL,
    precio_original     DECIMAL(9,2),
    cantidad_stock      INT           NOT NULL DEFAULT 0,
    url_imagen          MEDIUMTEXT,
    color               VARCHAR(30),
    rango_tallas        VARCHAR(20),
    destacado           BOOLEAN       NOT NULL DEFAULT FALSE,
    activo              BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion      DATETIME      NOT NULL,
    fecha_actualizacion DATETIME,
    categoria_id        BIGINT        NOT NULL,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_precio    CHECK (precio         >= 0),
    CONSTRAINT chk_stock     CHECK (cantidad_stock >= 0),
    INDEX idx_product_name     (nombre),
    INDEX idx_product_brand    (marca),
    INDEX idx_product_category (categoria_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: pedidos
-- Relación N:1 con usuarios
-- ============================================================
CREATE TABLE pedidos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido   VARCHAR(20)   NOT NULL UNIQUE,
    usuario_id      BIGINT        NOT NULL,
    total           DECIMAL(10,2) NOT NULL,
    estado          VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    direccion_envio VARCHAR(255),
    fecha_creacion  DATETIME      NOT NULL,
    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_estado CHECK (estado IN ('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED')),
    INDEX idx_pedido_usuario (usuario_id),
    INDEX idx_pedido_estado  (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: detalles_pedido
-- Tabla puente N:M entre pedidos y productos
-- ============================================================
CREATE TABLE detalles_pedido (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       BIGINT       NOT NULL,
    producto_id     BIGINT       NOT NULL,
    cantidad        INT          NOT NULL,
    precio_unitario DECIMAL(9,2) NOT NULL,
    talla           VARCHAR(10),
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id)   REFERENCES pedidos(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES productos(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Relaciones:
--   categorias  1 ---< N  productos       (1:N)
--   usuarios    1 ---< N  pedidos         (1:N)
--   pedidos     1 ---< N  detalles_pedido (1:N)
--   productos   1 ---< N  detalles_pedido (1:N)
-- ============================================================
