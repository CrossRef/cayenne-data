FROM ubuntu:14.04

COPY . /src

RUN apt-get update -qqy && \
    apt-get install -qqy openjdk-7-jre-headless && \
    apt-get install -qqy wget && \
    wget -q -O /usr/bin/lein \
      https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    chmod +x /usr/bin/lein

ENV LEIN_ROOT true

ENV SERVER_PORT 3000

EXPOSE 3000

WORKDIR /src
CMD lein run