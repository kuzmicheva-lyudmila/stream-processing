FROM openjdk:11-slim
COPY ./build/libs/notification-service-latest.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "notification-service-latest.jar"]
EXPOSE 8084