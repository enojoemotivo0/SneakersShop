-- ============================================================
-- SNIKERS SHOP — Datos iniciales
-- IMPORTANTE: DataInitializer.java ya carga estos datos al
-- arrancar la app si la BD está vacía. Este script sirve si
-- prefieres inicializar la BD por fuera (p.ej. backup/restore).
-- ============================================================

USE snikersdb;

-- Categorías
INSERT INTO categorias (name, slug, description) VALUES
('Running',    'running',    'Sneakers ligeras con amortiguación máxima para correr largas distancias.'),
('Basketball', 'basketball', 'Zapatillas de baloncesto con sujeción total y tracción agresiva.'),
('Lifestyle',  'lifestyle',  'Sneakers icónicas pensadas para el día a día, con estilo urbano.'),
('Skate',      'skate',      'Modelos resistentes diseñados para skaters: suela vulcanizada y durabilidad.');

-- Productos (8 sneakers)
INSERT INTO productos (name, brand, description, price, original_price, stock, color, size_range, image_url, featured, active, created_at, updated_at, category_id) VALUES
('Air Prime Nova 91',   'Aeroline', 'Amortiguación de aire visible, upper de malla ingeniería y suela de espuma energy-return.', 189.99, 229.99, 42,  'Negro / Volt',    '38-47', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 1),
('Court Revolt Hyper',  'Onyx',     'Zapatilla de basket con placa de carbono, cuello alto y tracción radial.',                219.00, NULL,   18,  'Rojo Fuego',      '39-48', 'https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 2),
('Metro Classic Retro', 'Urbano',   'Icono del streetwear reinventado. Piel premium, cordones planos.',                       129.95, NULL,   65,  'Blanco Roto',     '36-45', 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 3),
('Deck Pro Vulcan',     'Shred',    'Suela vulcanizada para máximo grip al tabla. Refuerzo doble en ollie area.',              89.99, 109.99, 120, 'Negro / Blanco',  '37-46', 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 4),
('Velocity Carbon X2',  'Aeroline', 'La más rápida de la serie. Placa de fibra de carbono y espuma PEBA.',                    259.00, NULL,    8,  'Azul Eléctrico',  '40-46', 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 1),
('High Top Legacy',     'Onyx',     'Modelo retro de baloncesto de los 80 reinterpretado en piel suave.',                    159.50, NULL,    25, 'Negro Monocromo', '39-46', 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=1200&q=80', FALSE, TRUE, NOW(), NOW(), 2),
('Cloudwalker Lite',    'Urbano',   'Hecha para caminar horas. Entresuela de espuma reactiva, peso pluma.',                    99.99, NULL,    88, 'Gris Niebla',     '36-46', 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=1200&q=80', FALSE, TRUE, NOW(), NOW(), 3),
('Grind Master SL',     'Shred',    'Silueta slim para skaters de precisión. Gamuza reforzada.',                               74.95, NULL,   140, 'Verde Oliva',     '37-45', 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=1200&q=80', FALSE, TRUE, NOW(), NOW(), 4);

-- Usuarios (Los usuarios por defecto han sido eliminados por petición del usuario)

