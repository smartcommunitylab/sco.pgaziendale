FROM openjdk:11-jdk

WORKDIR /app

ADD . /app

EXPOSE 8080
CMD ./mvnw package -Dmaven.test.skip=true && cd target && java -jar pgazienda-0.0.1-SNAPSHOT.jar