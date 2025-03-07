FROM openjdk:23

COPY target/camunda-7-experiment-*.jar /demo.jar

CMD ["java", "-jar", "/demo.jar"]