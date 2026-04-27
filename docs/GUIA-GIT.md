# Guía Git - Snikers Shop

Secuencia de commits profesional para cumplir la rúbrica y mantener un historial claro. Esta guía está pensada para un despliegue local sin Docker.

## Inicialización

```bash
cd snikers-shop
git init
git branch -M main
git config user.name "Tu Nombre"
git config user.email "tu@email.com"
```

## Secuencia de commits sugerida

### Commit 1 - Estructura inicial

```bash
git add pom.xml .gitignore README.md
git add src/main/java/com/snikers/shop/SnikersShopApplication.java
git add src/main/resources/application*.properties
git commit -m "chore: inicializacion del proyecto Spring Boot"
```

### Commit 2 - Modelo y persistencia

```bash
git add src/main/java/com/snikers/shop/model/
git add src/main/java/com/snikers/shop/repository/
git add src/main/java/com/snikers/shop/dto/
git add sql/
git add docs/diagrama-er.md
git commit -m "feat(data): entidades, repositorios y scripts SQL"
```

### Commit 3 - Servicios y seguridad

```bash
git add src/main/java/com/snikers/shop/service/
git add src/main/java/com/snikers/shop/config/
git commit -m "feat(core): servicios de negocio y seguridad"
```

### Commit 4 - Controladores MVC

```bash
git add src/main/java/com/snikers/shop/controller/
git commit -m "feat(web): controladores y flujo MVC"
```

### Commit 5 - Frontend y plantillas

```bash
git add src/main/resources/templates/
git add src/main/resources/static/
git commit -m "feat(ui): interfaz y experiencia de usuario"
```

### Commit 6 - Documentación y scripts de arranque

```bash
git add docs/ MEMORIA-APLICACION.md SOLUCIONAR_ERRORES.md iniciar-app.ps1
git commit -m "docs: documentacion tecnica y guia de ejecucion"
```

## Publicar en GitHub

```bash
git remote add origin https://github.com/TU-USUARIO/snikers-shop.git
git push -u origin main
```

## Verificación rápida tras clone

```bash
# Windows
.\iniciar-app.ps1

# Alternativa manual
.\mvnw.cmd spring-boot:run
```

La aplicación debe quedar disponible en http://localhost:8080.

## Guion de demo

1. Abrir la home y mostrar el hero slider.
2. Ir a catálogo, filtrar por categoría y abrir detalle.
3. Añadir al carrito y completar checkout.
4. Entrar con usuario cliente y revisar pedidos.
5. Entrar con admin y actualizar estado de pedido.

## Tips para la defensa

1. Explica primero la arquitectura en capas.
2. Justifica decisiones clave como soft-delete y BCrypt.
3. Enseña una clase por capa: controller, service y repository.
4. Cierra con una prueba funcional completa del flujo de compra.
