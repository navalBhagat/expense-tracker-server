# Use an official OpenJDK base image with Alpine Linux
FROM openjdk:17-oracle

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application JAR file into the container
COPY build/libs/expense-tracker-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application will run on (default is 8080)
EXPOSE 8080

# Specify the command to run your Spring Boot application
CMD ["java", "-jar", "app.jar"]
