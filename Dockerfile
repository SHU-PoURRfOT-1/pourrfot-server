FROM adoptopenjdk:11-jre-hotspot
COPY build/libs/pourrfot-server-*.jar app.jar
EXPOSE 9000
CMD ["java", "-Dcom.sun.management.jmxremote", "-jar", "app.jar"]
