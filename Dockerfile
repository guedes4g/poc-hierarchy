FROM maven:3.6.3-jdk-11-slim as build

WORKDIR /hierarchy-backend/
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline

COPY ./src/ ./src/
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim-buster as deploy

WORKDIR /hierarchy-backend/

COPY --from=build /hierarchy-backend/target/**.jar ./app.jar

EXPOSE 8080
CMD ["java","-jar","/hierarchy-backend/app.jar"]
