FROM openjdk:11-slim
COPY ./build/libs/auth-service-latest.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "auth-service-latest.jar"]
EXPOSE 8080
