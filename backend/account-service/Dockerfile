# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
# Uses a full Maven + JDK 26 image to compile and package the application.
# Dependencies are pulled in a separate layer so the cache is reused on
# subsequent builds when only source files change.
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-26 AS build

WORKDIR /app

# 1) Copy only the POM first and download all dependencies.
#    This layer is cached as long as pom.xml does not change.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# 2) Copy source and build the fat JAR (tests are run in CI, not here).
COPY src ./src
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime
# Uses a minimal JRE-only image to keep the final image small and reduce the
# attack surface. The application runs as a non-root user (uid 1001).
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:26-jre-alpine

# Create a dedicated non-root user and group for the process
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the fat JAR from the build stage
COPY --from=build /app/target/account-service-1.0.0.jar app.jar

# Own the working directory so the non-root user can write temp files if needed
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose the application port
EXPOSE 8080

# HEALTHCHECK for container orchestration
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM flags:
#   -XX:+UseContainerSupport       — respect cgroup CPU/memory limits inside Docker
#   -XX:MaxRAMPercentage=75.0      — use up to 75% of cgroup memory for the heap
#   -Djava.security.egd=...        — faster SecureRandom seeding (avoids /dev/random blocking)
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "/app/app.jar"]
