FROM openjdk:8-jre-alpine
LABEL maintainer="bishoy"
WORKDIR /webapp
ARG REPORTER_JAR
COPY ${REPORTER_JAR} /webapp
ADD https://raw.githubusercontent.com/eficode/wait-for/master/wait-for /
RUN chmod 555 /wait-for
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT java -jar reporter.jar