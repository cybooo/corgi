FROM openjdk:8-jdk-alpine

MAINTAINER "MrWakeCZ"

#Create working direcotory
RUN mkdir -p /srv/corgibot
WORKDIR /srv/corgibot

# Add basic files
ADD /target/CorgiBot-1.3.jar CorgiBot.jar
ADD facts.txt facts.txt

ENTRYPOINT ["java", "-jar", "CorgiBot.jar"]