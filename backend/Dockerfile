# syntax=docker/dockerfile:experimental
FROM maven:3-jdk-11 AS build
COPY ./src /tmp/src
COPY ./pom.xml /tmp/pom.xml
WORKDIR /tmp
RUN --mount=type=cache,target=/root/.m2,source=/root/.m2,from=smartcommunitylab/pgaziendale-backend:cache \ 
    mvn package -DskipTests

FROM eclipse-temurin:11-jdk-alpine
ENV APP=pgazienda-0.0.1-SNAPSHOT.jar
ARG USER=pgazienda
ARG USER_ID=1003
ARG USER_GROUP=pgazienda
ARG USER_GROUP_ID=1003
ARG USER_HOME=/home/${USER}

RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR  ${USER_HOME}
COPY --chown=pgazienda:pgazienda --from=build /tmp/target/*.jar ${USER_HOME}/${APP}
USER pgazienda
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP}"]
