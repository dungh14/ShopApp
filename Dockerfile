FROM eclipse-temurin:21-jdk

ARG FILE_JAR=target/*.jar

COPY ${FILE_JAR} shopapp-service.jar

ENTRYPOINT ["java", "-jar", "shopapp-service.jar"]

EXPOSE 8080