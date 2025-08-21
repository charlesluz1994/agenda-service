FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/agenda-*.jar app.jar
EXPOSE 8087

ENV JAVA_MAX_MEM=800m
ENV JAVA_OPTS="-server \
               -Xmx${JAVA_MAX_MEM} \
               -XX:+UseContainerSupport \
               -XX:+UseG1GC \
               -XX:MaxGCPauseMillis=200 \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -XX:+UseCompressedOops \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.backgroundpreinitializer.ignore=true"

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s CMD curl -f http://localhost:8087/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

# JVM arguments optimized for containers and Java 11
# Memory recommendations for different environments:
# Development/Small: 512MB (MaxRAM=400MB)  -> -Xmx400m
# Production/Medium: 1GB (MaxRAM=800MB)   -> -Xmx800m
# Production/Large: 2GB (MaxRAM=1600MB)   -> -Xmx1600m