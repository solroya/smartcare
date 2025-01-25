# 1. 베이스 이미지 설정 (Java 런타임 환경)
FROM openjdk:21-jdk-slim

# 2. 애플리케이션 JAR 파일을 컨테이너에 복사
ARG JAR_FILE=build/libs/smartcare-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너 시작 시 실행할 명령
#ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT ["java", "-Dserver.port=80", "-jar", "app.jar"]

# 4. 애플리케이션이 사용할 포트 노출
#EXPOSE 8080
EXPOSE 80