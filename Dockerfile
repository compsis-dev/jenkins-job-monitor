FROM azul/zulu-openjdk-alpine:11-jre

ENV HTTP_PORT=8080

EXPOSE $HTTP_PORT
ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/javalin/app.jar"]

HEALTHCHECK --interval=5s --timeout=5s --retries=3 \
  CMD wget localhost:8080 -q -O - > /dev/null 2>&1

ARG JAR_FILE
ADD target/$JAR_FILE  /usr/share/javalin/app.jar
ADD target/lib        /usr/share/javalin/lib

WORKDIR /usr/share/javalin

RUN mkdir /usr/share/javalin/config
VOLUME ["/usr/share/javalin/config"]