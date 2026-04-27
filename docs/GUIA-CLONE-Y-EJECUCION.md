# Guía completa de clone y ejecución local

Esta guía explica cómo clonar y arrancar Snikers Shop en otro ordenador, sin contenedores.

## 1) Requisitos mínimos

- Git instalado
- JDK 17 o superior instalado
- MySQL instalado y arrancado
- Puerto 8080 libre

## 2) Comprobar Java en la máquina

En Windows PowerShell:

java -version

Si no aparece versión, instala JDK 17+ y configura JAVA_HOME.

## 3) Clonar el repositorio

git clone https://github.com/enojoemotivo0/SneakersShop.git
cd Sneaker-shop2

## 4) Preparar base de datos

Arranca MySQL y crea la base:

CREATE DATABASE tienda;

Usuario esperado por defecto en dev:
- user: root
- password: vacía

Si en tu máquina root tiene contraseña, actualiza src/main/resources/application-dev.properties.

## 5) Arranque recomendado en Windows

.\iniciar-app.ps1

Este script intenta detectar JAVA_HOME y lanzar Maven Wrapper.

## 6) Arranque manual alternativo

En Windows:

.\mvnw.cmd spring-boot:run

En Linux/macOS:

./mvnw spring-boot:run

## 7) Verificación funcional

- Abre http://localhost:8080
- Comprueba que carga la home
- Haz login de prueba:
  - Admin: admin@snikers.shop / admin1234
  - Cliente: cliente@snikers.shop / cliente123

## 8) Pruebas rápidas de flujo

1. Entrar al catálogo y abrir un producto
2. Añadir al carrito
3. Completar checkout
4. Revisar pedidos en área de usuario

## 9) Problemas frecuentes

### Error JAVA_HOME not found

- Instala JDK 17+
- Define JAVA_HOME a la carpeta del JDK
- Reinicia terminal

### Error de conexión a MySQL

- Verifica que MySQL está levantado
- Verifica base de datos tienda
- Revisa usuario y contraseña en application-dev.properties

### Puerto 8080 ocupado

- Cierra el proceso que usa el puerto
- O cambia server.port en application.properties

## 10) Comandos útiles

Ejecutar tests:

.\mvnw.cmd test

Compilar sin ejecutar:

.\mvnw.cmd clean package
