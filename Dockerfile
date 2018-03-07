FROM openjdk:8-jdk-alpine

RUN mkdir -p /srv/corgibot
WORKDIR /srv/corgibot

ADD /target/CorgiBot-1.3.jar CorgiBot.jar

ENTRYPOINT ["java", "-jar", "CorgiBot.jar"]