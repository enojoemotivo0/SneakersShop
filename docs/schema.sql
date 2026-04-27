-- ============================================================
-- SNIKERS SHOP — Script de creación de base de datos
-- Motor: MySQL 8.0
-- Autor: Proyecto Transversal Final · 2º DAM/DAW
-- ============================================================

-- Crear base de datos (el contenedor la crea con las env vars,
-- pero este comando permite ejecutar el script manualmente)
CREATE DATABASE IF NOT EXISTS tienda
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tienda;

-- ============================================================
-- Tabla: categories
-- ============================================================
DROP TABLE IF EXISTS detalles_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    slug        VARCHAR(100),
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: users
-- ============================================================
CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name  VARCHAR(80)  NOT NULL,
    email      VARCHAR(120) NOT NULL UNIQUE,
    password   VARCHAR(120) NOT NULL,
    phone      VARCHAR(20),
    address    VARCHAR(255),
    role       VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER',
    created_at DATETIME     NOT NULL,
    CONSTRAINT chk_role CHECK (role IN ('CUSTOMER','ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: products
-- Relación N:1 con categories
-- ============================================================
CREATE TABLE products (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(120)   NOT NULL,
    brand          VARCHAR(60)    NOT NULL,
    description    VARCHAR(2000),
    price          DECIMAL(9,2)   NOT NULL,
    original_price DECIMAL(9,2),
    stock          INT            NOT NULL DEFAULT 0,
    image_url      VARCHAR(255),
    color          VARCHAR(30),
    size_range     VARCHAR(20),
    featured       BOOLEAN        NOT NULL DEFAULT FALSE,
    active         BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at     DATETIME       NOT NULL,
    updated_at     DATETIME,
    category_id    BIGINT         NOT NULL,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_price CHECK (price >= 0),
    CONSTRAINT chk_stock CHECK (stock >= 0),
    INDEX idx_product_name (name),
    INDEX idx_product_brand (brand),
    INDEX idx_product_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: orders
-- Relación N:1 con users
-- ============================================================
CREATE TABLE orders (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number      VARCHAR(20)  NOT NULL UNIQUE,
    user_id           BIGINT       NOT NULL,
    total             DECIMAL(10,2) NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    shipping_address  VARCHAR(255),
    created_at        DATETIME     NOT NULL,
    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED')),
    INDEX idx_order_user (user_id),
    INDEX idx_order_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Tabla: order_items (tabla puente N:M entre orders y products)
-- Relaciones N:1 con orders y N:1 con products
-- ============================================================
CREATE TABLE order_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    quantity    INT           NOT NULL,
    unit_price  DECIMAL(9,2)  NOT NULL,
    size        VARCHAR(10),
    CONSTRAINT fk_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_product
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_quantity CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Resumen de relaciones:
-- - categories  1 ---< N  products    (relación 1:N)
-- - users       1 ---< N  orders      (relación 1:N)
-- - orders      1 ---< N  order_items (relación 1:N)
-- - products    1 ---< N  order_items (relación 1:N)
-- ============================================================
