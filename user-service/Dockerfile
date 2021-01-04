FROM openjdk:11-slim
COPY ./build/libs/user-service-latest.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "user-service-latest.jar"]
EXPOSE 8081
