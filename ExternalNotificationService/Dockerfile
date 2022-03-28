FROM openjdk:16
LABEL maintainer="tecnotree.com"
ADD target/NotificacionAlternateService-0.0.1-SNAPSHOT.jar NotificacionAlternateService.jar
#RUN addgroup -S spring && adduser - S spring -G spring
#USER spring:spring
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} NotificacionAlternateService-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "NotificacionAlternateService-0.0.1.jar"]