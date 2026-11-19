FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew bootJar

EXPOSE 8080

CMD ["sh", "-c", "java -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -jar build/libs/*.jar"]