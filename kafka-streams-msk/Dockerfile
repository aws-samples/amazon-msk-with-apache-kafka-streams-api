FROM amazoncorretto:21-alpine
RUN apk add libstdc++
ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} kafka-streams-msk.jar

ENTRYPOINT ["java","-jar","-Xmx2g", "-Xms2g","/kafka-streams-msk.jar"]