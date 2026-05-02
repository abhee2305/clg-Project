# cache-bust: v2
# ─────────────────────────────────────────
#  Stage 1: BUILD — Maven + Java 17
# ─────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (Docker caches this layer — faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ─────────────────────────────────────────
#  Stage 2: RUN — Lightweight Java 17 only
# ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from Stage 1
COPY --from=build /app/target/securetest-1.0.0.jar app.jar

# Render injects PORT env var automatically
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
