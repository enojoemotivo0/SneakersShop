```mermaid
erDiagram
    CATEGORIES ||--o{ PRODUCTS : "contiene"
    USERS      ||--o{ ORDERS   : "realiza"
    ORDERS     ||--o{ ORDER_ITEMS : "incluye"
    PRODUCTS   ||--o{ ORDER_ITEMS : "figura en"

    CATEGORIES {
        BIGINT       id PK
        VARCHAR(50)  name UK
        VARCHAR(100) slug
        VARCHAR(255) description
    }

    PRODUCTS {
        BIGINT        id PK
        VARCHAR(120)  name
        VARCHAR(60)   brand
        VARCHAR(2000) description
        DECIMAL       price
        DECIMAL       original_price
        INT           stock
        VARCHAR(255)  image_url
        VARCHAR(30)   color
        VARCHAR(20)   size_range
        BOOLEAN       featured
        BOOLEAN       active
        DATETIME      created_at
        DATETIME      updated_at
        BIGINT        category_id FK
    }

    USERS {
        BIGINT       id PK
        VARCHAR(80)  full_name
        VARCHAR(120) email UK
        VARCHAR(120) password
        VARCHAR(20)  phone
        VARCHAR(255) address
        VARCHAR(20)  role
        DATETIME     created_at
    }

    ORDERS {
        BIGINT       id PK
        VARCHAR(20)  order_number UK
        BIGINT       user_id FK
        DECIMAL      total
        VARCHAR(20)  status
        VARCHAR(255) shipping_address
        DATETIME     created_at
    }

    ORDER_ITEMS {
        BIGINT      id PK
        BIGINT      order_id FK
        BIGINT      product_id FK
        INT         quantity
        DECIMAL     unit_price
        VARCHAR(10) size
    }
```

## Modelo Entidad-Relación · Snikers Shop

### Entidades y cardinalidades

| Relación | Cardinalidad | Lectura |
|----------|--------------|---------|
| Categories — Products | 1 : N | Una categoría tiene N productos; un producto pertenece a 1 categoría |
| Users — Orders | 1 : N | Un usuario realiza N pedidos; un pedido pertenece a 1 usuario |
| Orders — Order_Items | 1 : N | Un pedido incluye N líneas; cada línea pertenece a 1 pedido |
| Products — Order_Items | 1 : N | Un producto figura en N líneas de pedido |

La tabla `order_items` actúa como **tabla puente** en una relación N:M entre `orders` y `products`, guardando además atributos propios (cantidad, talla, precio unitario).

### Claves

- **Primarias:** todas las tablas usan `id BIGINT AUTO_INCREMENT`.
- **Foráneas:**
  - `products.category_id → categories.id`
  - `orders.user_id → users.id`
  - `order_items.order_id → orders.id`
  - `order_items.product_id → products.id`
- **Únicas:** `categories.name`, `users.email`, `orders.order_number`.
