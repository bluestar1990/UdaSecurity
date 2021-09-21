FROM openjdk:12-alpine
COPY target/springboot-in-10-steps-*.jar /springboot-in-10-steps.jar
CMD ["java", "-jar", "springboot-in-10-steps.jar"]