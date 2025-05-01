FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application with clean to ensure fresh build
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Copy the JAR file
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Copy the entrypoint script
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Run the application
ENTRYPOINT ["/docker-entrypoint.sh"]
