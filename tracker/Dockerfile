FROM openjdk:8-jre-alpine
LABEL maintainer="bishoy"
WORKDIR /webapp
ARG TRACKER_JAR
ADD https://raw.githubusercontent.com/eficode/wait-for/master/wait-for /
RUN chmod 555 /wait-for
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=test
COPY ${TRACKER_JAR} /webapp
ENTRYPOINT java -jar tracker.jar