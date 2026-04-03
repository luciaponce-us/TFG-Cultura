FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY backend/.mvn/ .mvn
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
RUN ./mvnw -DskipTests package
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar target/*.jar"]