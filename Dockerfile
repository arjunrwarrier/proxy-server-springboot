# Use Eclipse Temurin (OpenJDK) base image
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/server-*.jar app.jar

# Expose port 9090 (or 9091, if using that)
EXPOSE 9091

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
