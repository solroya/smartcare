# 베이스 이미지로 Java 21 슬림 버전 사용
FROM openjdk:21-slim

# 메타데이터 추가 - 유지보수를 위한 정보
LABEL maintainer="ChulHee Kim <solroya@gmail.com>"
LABEL application="smartcare"
LABEL version="1.0"

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (와일드카드 사용)
COPY build/libs/smartcare*.jar app.jar

# 애플리케이션 포트 설정
EXPOSE 8080

# Java 옵션 설정을 위한 환경 변수
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]