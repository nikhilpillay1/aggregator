FROM maven:3.9.11-amazoncorretto-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM amazoncorretto:17-alpine

WORKDIR /app

COPY --from=build /app/target/aggregator-*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/aggregator_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=password