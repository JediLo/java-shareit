FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
ENV JAVA_OPTS="-Duser.timezone=UTC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

