FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY backend/.mvn/ .mvn
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
EXPOSE 8080
CMD ["./mvnw", "spring-boot:run"]