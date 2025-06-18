FROM eclipse-temurin:17


LABEL maintainer="kanhnupolai79@gmail.com"

WORKDIR /app


COPY target/authservice-0.0.1-SNAPSHOT.jar /app/authservice.jar
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "authservice.jar" ]