FROM openjdk:11-slim

# Oooo KWAK!
MAINTAINER "MrWakeCZ"

WORKDIR /app

COPY . /app

# Build
RUN ./gradlew shadowJar

# List files in build folder
RUN ls build/libs

# Entrypoint for start
ENTRYPOINT ["java", "-jar", "build/libs/corgibot.jar"]