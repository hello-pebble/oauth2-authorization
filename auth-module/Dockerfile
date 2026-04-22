# Build Stage for Auth Module
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# 1. Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 2. 소스 코드 복사 (전체 프로젝트 루트 기준)
COPY auth-module auth-module

# 3. 빌드 권한 부여 및 빌드 수행 (Auth 전용)
RUN chmod +x gradlew && ./gradlew :auth-module:bootJar --no-daemon

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 4. 빌드 스테이지에서 생성된 Auth JAR 복사
COPY --from=build /app/auth-module/build/libs/*.jar app.jar

# Auth 서비스는 8080 포트를 사용합니다.
EXPOSE 8080

# 5. 실행 설정 (DB 연결 및 JWT 환경변수는 Render 대시보드에서 주입)
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
