FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY backend/pom.xml .
COPY backend/.mvn .mvn
COPY backend/mvnw .

RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY backend/src ./src

RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]