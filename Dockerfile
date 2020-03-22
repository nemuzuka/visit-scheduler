FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD build/libs/visit-scheduler-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=8080","-jar","/app.jar"]
