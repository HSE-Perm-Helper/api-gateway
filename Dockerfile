FROM openjdk:17-jdk-slim

COPY api-gateway-standalone.jar .

ENTRYPOINT ["java", "-jar", "api-gateway-standalone.jar"]