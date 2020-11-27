# syntax=docker/dockerfile:experimental
FROM maven:3-jdk-11 AS mvn
COPY . /backend
WORKDIR /backend
RUN --mount=type=bind,target=/root/.m2,source=/root/.m2,from=smartcommunitylab/aac-org:cache-alpine mvn package -DskipTests

FROM adoptopenjdk/openjdk11:alpine
ENV APP=pgazienda-0.0.1-SNAPSHOT.jar
ARG USER=pgazienda
ARG USER_ID=1003
ARG USER_GROUP=pgazienda
ARG USER_GROUP_ID=1003
ARG USER_HOME=/home/${USER}

RUN  addgroup -g ${USER_GROUP_ID} ${USER_GROUP}; \
     adduser -u ${USER_ID} -D -g '' -h ${USER_HOME} -G ${USER_GROUP} ${USER} ;

WORKDIR  ${USER_HOME}
COPY --chown=pgazienda:pgazienda --from=mvn /backend/target/*.jar ${USER_HOME}/${APP}
USER pgazienda
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ${APP}"]