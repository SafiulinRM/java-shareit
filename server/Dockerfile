FROM amazoncorretto:11
ARG JAR_FILE=target/shareit-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} shareit-server.jar
ENTRYPOINT ["java","-jar","shareit-server.jar"]