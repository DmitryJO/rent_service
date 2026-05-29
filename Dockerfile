# ========== СТАДИЯ 1: СБОРКА ==========
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэшируется Docker'ом)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ========== СТАДИЯ 2: ЗАПУСК ==========
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем собранный JAR из стадии builder
COPY --from=builder /app/target/*.jar app.jar

# Открываем порт (обычно 8080 для Spring Boot)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]