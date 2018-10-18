FROM openjdk:8-jre-alpine
LABEL maintainer="bishoy"
WORKDIR /webapp
ARG REPORTER_JAR
ADD https://raw.githubusercontent.com/eficode/wait-for/master/wait-for /
RUN chmod 555 /wait-for
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=test
COPY ${REPORTER_JAR} /webapp
ENTRYPOINT java -jar reporter.jar