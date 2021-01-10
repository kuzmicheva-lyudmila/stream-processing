FROM openjdk:11-slim
COPY ./build/libs/order-service-latest.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "order-service-latest.jar"]
EXPOSE 8083