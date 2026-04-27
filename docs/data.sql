-- ============================================================
-- SNIKERS SHOP — data.sql
-- Datos iniciales de ejemplo para la tienda
-- IMPORTANTE: DataInitializer.java carga estos datos automáticamente
-- al arrancar si la BD está vacía. Usar este script solo para
-- inicialización manual o restauración de datos.
-- Ejecutar DESPUÉS de init.sql
-- ============================================================

USE tienda;

-- ============================================================
-- Categorías
-- Columnas: nombre, descripcion, url_amigable
-- ============================================================
INSERT INTO categorias (nombre, descripcion, url_amigable) VALUES
('Running',    'Sneakers ligeras con amortiguación máxima para correr largas distancias.', 'running'),
('Basketball', 'Zapatillas de baloncesto con sujeción total y tracción agresiva.',          'basketball'),
('Lifestyle',  'Sneakers icónicas pensadas para el día a día, con estilo urbano.',          'lifestyle'),
('Skate',      'Modelos resistentes diseñados para skaters: suela vulcanizada y durabilidad.', 'skate');

-- ============================================================
-- Productos (8 sneakers)
-- Columnas: nombre, marca, descripcion, precio, precio_original,
--           cantidad_stock, color, rango_tallas, url_imagen,
--           destacado, activo, fecha_creacion, fecha_actualizacion,
--           categoria_id
-- ============================================================
INSERT INTO productos
  (nombre, marca, descripcion, precio, precio_original, cantidad_stock,
   color, rango_tallas, url_imagen, destacado, activo,
   fecha_creacion, fecha_actualizacion, categoria_id)
VALUES
('Air Prime Nova 91',   'Aeroline', 'Amortiguación de aire visible, upper de malla ingeniería y suela de espuma energy-return.', 189.99, 229.99, 42,  'Negro / Volt',    '38-47', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 1),
('Court Revolt Hyper',  'Onyx',     'Zapatilla de basket con placa de carbono, cuello alto y tracción radial.',                  219.00, NULL,   18,  'Rojo Fuego',      '39-48', 'https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 2),
('Metro Classic Retro', 'Urbano',   'Icono del streetwear reinventado. Piel premium, cordones planos.',                         129.95, NULL,   65,  'Blanco Roto',     '36-45', 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 3),
('Deck Pro Vulcan',     'Shred',    'Suela vulcanizada para máximo grip al tabla. Refuerzo doble en ollie area.',                89.99,  109.99, 120, 'Negro / Blanco',  '37-46', 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 4),
('Velocity Carbon X2',  'Aeroline', 'La más rápida de la serie. Placa de fibra de carbono y espuma PEBA.',                      259.00, NULL,    8,  'Azul Eléctrico',  '40-46', 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=1200&q=80', TRUE,  TRUE, NOW(), NOW(), 1),
('High Top Legacy',     'Onyx',     'Modelo retro de baloncesto de los 80 reinterpretado en piel suave.',                      159.50, NULL,   25,  'Negro Monocromo', '39-46', 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=1200&q=80', FALSE, TRUE, NOW(), NOW(), 2),
('Cloudwalker Lite',    'Urbano',   'Hecha para caminar horas. Entresuela de espuma reactiva, peso pluma.',                     99.99,  NULL,   88,  'Gris Niebla',     '36-46', 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=1200&q=80', FALSE, TRUE, NOW(), NOW(), 3),
('Grind Master SL',     'Shred',    'Silueta slim para skaters de precisión. Gamuza reforzada.',                                74.95,  NULL,  140,  'Verde Oliva',     '37-45', 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=1200&q=80', FALSE, TRUE, NOW(), NOW(), 4);

-- ============================================================
-- Usuarios de demo
-- NOTA: Las contraseñas aquí son texto plano solo como referencia.
-- La aplicación siempre encripta con BCrypt. Para insertar
-- manualmente necesitas un hash BCrypt real.
-- Credenciales: admin@snikers.shop / admin1234
--               cliente@snikers.shop / cliente123
-- (DataInitializer.java las crea automáticamente con hash correcto)
-- ============================================================

