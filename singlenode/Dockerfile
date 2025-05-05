FROM openjdk:17-jdk-slim
VOLUME /tmp
RUN apt-get update \
    && apt-get install -y curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
COPY target/*.jar app.jar
HEALTHCHECK --interval=10s --timeout=3s --start-period=10s --retries=5 \
  CMD curl -f http://localhost:9090/actuator/health || exit 1
ENTRYPOINT ["java","-XX:-UseContainerSupport","-jar","/app.jar"] 