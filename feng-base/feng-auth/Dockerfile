FROM openjdk:17

RUN mkdir -p /feng/feng-cloud
WORKDIR /feng/feng-cloud

COPY target/feng-auth.jar .
ENV NACOS_ADDR=192.168.40.2:8848
ENV JEP=Feng@2025

EXPOSE 2000

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Xms128m", "-Xmx256m", \
           "-Ddruid.mysql.usePingMethod=false", "-jar", "feng-auth.jar", \
           "--spring.profiles.active=dev"]
