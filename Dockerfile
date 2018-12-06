FROM openjdk:10-slim

# Oooo KWAK!
MAINTAINER "MrWakeCZ"

# Add basic files
COPY /build/libs/corgibot.jar /srv/corgibot.jar

WORKDIR /srv

# Basic run
ENTRYPOINT ["java", "-jar", "corgibot.jar"]