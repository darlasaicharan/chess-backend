# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy only the POM first to cache dependency downloads
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and package (skip tests — run them in CI, not at image build time)
COPY src ./src
RUN mvn package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S neonmate && adduser -S neonmate -G neonmate
USER neonmate

# Copy the fat JAR produced in the build stage
COPY --from=builder /app/target/neonmate-chess-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
