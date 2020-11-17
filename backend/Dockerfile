FROM openjdk:8-jdk

WORKDIR /app

ADD . /app
COPY docker-configs/application.yml src/main/resources/application.yml

EXPOSE 8080
CMD ./mvnw package -Dmaven.test.skip=true && cd target && java -jar internshipbrowser-0.1.jar