FROM openjdk:11-slim
COPY ./build/libs/billing-service-latest.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "billing-service-latest.jar"]
EXPOSE 8082