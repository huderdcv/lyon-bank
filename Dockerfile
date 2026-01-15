# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create directory for certs
RUN mkdir -p /app/certs

# Create a startup script to write keys from Env Vars to files
RUN echo '#!/bin/sh' > /entrypoint.sh && \
    echo 'echo "$RSA_PRIVATE_KEY" > /app/certs/private.pem' >> /entrypoint.sh && \
    echo 'echo "$RSA_PUBLIC_KEY" > /app/certs/public.pem' >> /entrypoint.sh && \
    echo 'exec java -jar app.jar' >> /entrypoint.sh && \
    chmod +x /entrypoint.sh

# Expose port
EXPOSE 8080

# Run the script
ENTRYPOINT ["/entrypoint.sh"]