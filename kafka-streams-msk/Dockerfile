FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} kafka-streams-msk.jar

ENTRYPOINT ["java","-jar","/kafka-streams-msk.jar"]