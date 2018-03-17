FROM openjdk:8-jdk-alpine@sha256:4cd17a64b67df1a929a9c6dedf513afcdc48f3ca0b7fddee6489d0246a14390b

# Oooo KWAK!
MAINTAINER "MrWakeCZ"

# Add basic files
ADD /target/CorgiBot-1.3.jar corgibot.jar
ADD facts.txt facts.txt

# Basic run
ENTRYPOINT ["java", "-jar", "corgibot.jar"]