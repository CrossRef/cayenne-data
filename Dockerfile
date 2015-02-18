FROM ubuntu:14.04

COPY target/cayenne-data-0.1.0-standalone.jar /src/cayenne-data.jar

RUN apt-get update -qyy && apt-get install -qqy openjdk-7-jre-headless

ENV SERVER_PORT 3000
EXPOSE 3000

CMD java -jar /src/cayenne-data.jar