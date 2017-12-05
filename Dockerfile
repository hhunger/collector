FROM resin/rpi-raspbian:latest

ARG COLLECTOR_VERSION

RUN apt-get update && apt-get upgrade
RUN apt-get install openjdk-8-jre-headless

COPY build/libs/collector-${COLLECTOR_VERSION}-all.jar /collector.jar

CMD [ "java", "-jar", "/collector.jar" ]
