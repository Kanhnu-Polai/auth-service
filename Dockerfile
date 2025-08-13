FROM eclipse-temurin:17

LABEL maintainer="kanhnupolai79@gmail.com"

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
