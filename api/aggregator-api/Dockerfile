FROM amazoncorretto:17-alpine

WORKDIR /app

COPY target/aggregator-*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-container:5432/aggregator_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=password