FROM openjdk:8-jdk-alpine

# Oooo KWAK!
MAINTAINER "MrWakeCZ"

# Add basic files
ADD /target/CorgiBot-1.3.jar corgibot.jar
ADD facts.txt facts.txt

# Basic run
ENTRYPOINT ["java", "-jar", "corgibot.jar"]