#FROM eclipse-temurin
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app.jar"]

#
# Build stage
#
FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

#
# Package stage
#
FROM eclipse-temurin:17-jre
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

# Builder stage
#FROM eclipse-temurin:17-jdk AS builder
#WORKDIR /app
#COPY . .
#RUN ./gradlew build --no-daemon
#
## Runner stage
#FROM eclipse-temurin:17-jre
#WORKDIR /app
#COPY --from=builder /app/build/libs/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
