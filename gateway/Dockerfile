FROM amazoncorretto:11
ARG JAR_FILE=target/shareit-gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} shareit-gateway.jar
ENTRYPOINT ["java","-jar","shareit-gateway.jar"]