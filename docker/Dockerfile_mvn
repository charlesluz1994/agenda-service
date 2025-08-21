# Build stage
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN groupadd -g 1001 appuser && useradd -u 1001 -g appuser -m appuser
WORKDIR /app
COPY --from=builder --chown=appuser:appuser /app/target/agenda-*.jar app.jar
USER appuser
EXPOSE 8087
ENV JAVA_OPTS="-Xmx1400m -XX:+UseG1GC"
HEALTHCHECK --interval=30s CMD curl -f http://localhost:8087/actuator/health
#ENTRYPOINT ["dumb-init", "--"]
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]