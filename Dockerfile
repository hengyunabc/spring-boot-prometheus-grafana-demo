FROM openjdk:8-jdk-alpine
EXPOSE 8080
EXPOSE 8090
ARG JAR_FILE=target/expose-prometheus-demo-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]