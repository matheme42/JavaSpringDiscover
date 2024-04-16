FROM alpine:latest


RUN apk update
RUN apk add --no-cache java-cacerts openjdk17-jdk
RUN apk add maven

WORKDIR "/app"

EXPOSE 9001

CMD mvn spring-boot:run

