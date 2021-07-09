FROM openjdk:11-slim

# Oooo KWAK!
LABEL cz.wake.corgibot.image.authors="MrWakeCZ; cybooo"

# Add basic files
COPY ./build/libs/corgibot.jar /srv/corgibot.jar

WORKDIR /srv

# Basic run
ENTRYPOINT ["java", "-jar", "corgibot.jar"]