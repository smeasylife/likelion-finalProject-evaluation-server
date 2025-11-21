# 1단계: 빌드 (Builder)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# 캐싱 최적화
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 (Runtime) - Cloud Run 최적화
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Cloud Run은 8080이 기본이지만, 환경변수로 제어되는 것이 권장됨
ENV PORT=8080

# Spring Boot가 Cloud Run이 주는 $PORT를 인식하도록 설정
# server.port가 없으면 8080 사용
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dserver.port=${PORT}", "-jar", "app.jar"]