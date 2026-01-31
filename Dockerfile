# 1. Build Stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
# 테스트는 빌드 시 제외 (DB 연결 문제 방지)하거나, docker-compose에서 처리. 일단 skip
RUN ./gradlew build -x test --no-daemon

# 2. Run Stage
FROM openjdk:25-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
