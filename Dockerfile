FROM maven:3.8.5-openjdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#gcr.io/distroless/java - prove apenas o necessário para executar
#FROM openjdk:11-jre-slim
FROM gcr.io/distroless/java
COPY --from=build /home/app/target/authuser-0.0.1-SNAPSHOT.jar.jar authUser-service.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/authUser-service.jar"]