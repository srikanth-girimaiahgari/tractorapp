# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user (security best practice)
RUN groupadd -r tractorapp && useradd -r -g tractorapp tractorapp

COPY --from=builder /app/target/tractor-field-manager-1.0.0.jar app.jar

RUN chown tractorapp:tractorapp app.jar
USER tractorapp

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/api/dashboard/stats || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
