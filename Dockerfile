FROM azul/zulu-openjdk-alpine:11-jre

ENV HTTP_PORT=8080

EXPOSE $HTTP_PORT
ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/javalin/app.jar"]

HEALTHCHECK --interval=1m --timeout=3s \
  CMD curl -f http://localhost:$HTTP_PORT/ || exit 1

ARG JAR_FILE
ADD target/$JAR_FILE  /usr/share/javalin/app.jar
ADD target/lib        /usr/share/javalin/lib

WORKDIR /usr/share/javalin

RUN mkdir /usr/share/javalin/config
VOLUME ["/usr/share/javalin/config"]