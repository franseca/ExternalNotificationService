FROM openjdk:11
LABEL maintainer="tecnotree.com"
ADD target/ExternalNotificationService-0.0.1-SNAPSHOT.jar /usr/local/tecnoapp/ExternalNotificationService-0.0.1-SNAPSHOT.jar
#RUN addgroup -S spring && adduser - S spring -G spring
#USER spring:spring
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} NotificacionAlternateService-0.0.1-SNAPSHOT.jar
EXPOSE 9898
#ENV NUM_RULES = 4
ENTRYPOINT ["java", "-jar", "/usr/local/tecnoapp/ExternalNotificationService-0.0.1-SNAPSHOT.jar"]