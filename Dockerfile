# ============================================================
# Snikers Shop — Dockerfile multi-stage
# Stage 1: Build con Maven + JDK 17
# Stage 2: Runtime ligero con JRE 17 Alpine
# ============================================================

# -------- STAGE 1: build --------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Cachear dependencias: copiamos solo el pom.xml primero
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar saltando tests (se ejecutan en CI aparte)
RUN mvn clean package -DskipTests -B

# -------- STAGE 2: runtime --------
FROM eclipse-temurin:17-jre-alpine

# Crear usuario no-root para mayor seguridad
RUN addgroup -S snikers && adduser -S snikers -G snikers

WORKDIR /app

# Copiar el JAR construido
COPY --from=builder /build/target/snikers-shop.jar app.jar

# Cambiar propiedad al usuario snikers
RUN chown -R snikers:snikers /app

USER snikers

EXPOSE 8080

# Healthcheck para comprobar que la app responde
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/ || exit 1

# Activar perfil "prod" por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
