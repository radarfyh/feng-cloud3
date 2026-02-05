FROM openjdk:8-jre

MAINTAINER edison 
RUN mkdir /data/webserver -p
WORKDIR /data/webserver

ADD start.sh .
ADD feng-user2-biz/target/feng-user2-biz.jar .

RUN chmod u+x start.sh
ENV PROJECT DEV

EXPOSE 4000

ENTRYPOINT "/data/webserver/start.sh" ${PROJECT}
