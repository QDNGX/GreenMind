FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY backend/pom.xml ./backend/
RUN mvn -f backend/pom.xml dependency:go-offline

COPY backend/src ./backend/src
COPY frontend/ ./frontend/
COPY index.html ./

RUN mvn -f backend/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
