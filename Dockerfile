# Use a base image with Java 17 for the build stage
FROM eclipse-temurin:17-jdk-alpine as builder

# Set the working directory inside the container
WORKDIR /app

# Install Gradle manually in the builder stage
RUN apk add --no-cache bash curl unzip && \
    curl -L "https://services.gradle.org/distributions/gradle-8.3-bin.zip" -o gradle.zip && \
    unzip gradle.zip -d /opt && \
    ln -s /opt/gradle-8.3/bin/gradle /usr/bin/gradle && \
    rm gradle.zip

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle .
COPY build.gradle .
COPY gradle gradle

# Make gradlew executable
RUN chmod +x gradlew

# Run Gradle wrapper to download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the application source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Use a smaller image for the runtime
FROM eclipse-temurin:17-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your application runs on
EXPOSE 9090

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]