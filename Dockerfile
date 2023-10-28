FROM openjdk:17-alpine
MAINTAINER anni
COPY build/libs/boku-assignment-0.0.1-SNAPSHOT.jar boku-assignment-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/boku-assignment-0.0.1-SNAPSHOT.jar"]