FROM maven:3.6.2-jdk-11 as build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src/ ./src/
RUN mvn -B -q package
FROM openjdk:12
COPY --from=build target/foo-service.jar /opt
CMD [ "java", "-jar", "/opt/foo-service.jar" ]
