# Stage 1: Build the Java application using Maven
# Uses a Maven image with a specific Java version (e.g., OpenJDK 21)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files (pom.xml first to leverage Docker cache)
COPY pom.xml .
# Copy the source code
COPY src ./src

# Build the Spring Boot application
# -DskipTests: Skips running tests during the build (tests should ideally run in a separate CI stage)
# -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true: (Optional) If you face SSL issues with Maven Central, use these. Remove in production.
RUN mvn clean package -DskipTests

# Stage 2: Create a smaller runtime image
# Uses a lightweight OpenJDK JRE image (Java Runtime Environment)
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the 'build' stage
# The JAR file is typically found in target/ directory after 'mvn package'
COPY --from=build /app/target/*.jar app.jar

# Expose the port that your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Define the command to run the application when the container starts
# 'java -jar app.jar' executes the packaged Spring Boot application
CMD ["java", "-jar", "app.jar"]