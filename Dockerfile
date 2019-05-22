FROM azul/zulu-openjdk-alpine:11-jre

ENV HTTP_PORT=9000
WORKDIR /usr/share/jenkins-monitor

EXPOSE $HTTP_PORT

HEALTHCHECK --interval=5s --timeout=5s --retries=3 \
  CMD wget localhost:$HTTP_PORT -q -O - > /dev/null 2>&1

ARG JAR_FILE
ADD target/$JAR_FILE  /usr/share/jenkins-monitor/app.jar
ADD target/lib        /usr/share/jenkins-monitor/lib

RUN mkdir /usr/share/jenkins-monitor/config
VOLUME ["/usr/share/jenkins-monitor/config"]

ENTRYPOINT /usr/bin/java -jar -Xms256M -Xmx256M -Dserver.port=$HTTP_PORT /usr/share/jenkins-monitor/app.jar