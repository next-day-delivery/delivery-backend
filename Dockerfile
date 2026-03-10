FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle gradle.properties ./

RUN chmod +x ./gradlew

COPY src src

RUN ./gradlew --no-daemon build

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
