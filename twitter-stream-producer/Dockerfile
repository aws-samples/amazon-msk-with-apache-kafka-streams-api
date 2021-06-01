FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} twitter-stream-producer.jar

ENTRYPOINT ["java","-jar","-Xmx2g", "-Xms2g","/twitter-stream-producer.jar"]