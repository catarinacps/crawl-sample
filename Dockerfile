FROM maven:3.6.3-jdk-14

ADD . /usr/src/crawl
WORKDIR /usr/src/crawl
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]