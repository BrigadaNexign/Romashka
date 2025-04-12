FROM gradle:jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

FROM eclipse-temurin:17-jre
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]
