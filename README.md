# SmartCare Hospital Service

## 프로젝트 소개
스마트케어 병원 서비스는 환자와 의료진을 위한 인공지능 기반 웹 애플리케이션으로, 진료 관리부터 위치 기반 서비스까지 제공합니다.

## 주요 기능

### 환자 진료 관리 시스템
- 환자 등록 및 관리
- 진료 예약 및 일정 관리
- 진료 기록 저장 및 조회
- 접수 처리 및 상태 관리

### 경로 추천 시스템
- TMap API 활용 최적 경로 추천
- 도보/버스 복합 경로 제공
- 거리 및 소요시간 기반 지능형 추천

### 주변 음식점 정보 서비스
- 카테고리별 음식점 검색
- 위치 기반 추천
- 리뷰 및 평점 기능

### AI 분석 서비스
- OpenAI API 통합
- Local API 'Ollama Mistral'
- 진료 데이터 분석 '환자 기록카드'
- AI 사용 패턴 모니터링

## 기술 스택

### 백엔드
- Java 21
- Spring Boot 3.3.7
- Spring Security
- Spring Data JPA
- Spring AOP
- Oracle Database
- Redis

### 프론트엔드
- Thymeleaf
- Bootstrap
- JavaScript/jQuery

### 외부 API
- TMap API (경로 탐색)
- Kakao OAuth/Maps API
- CoolSMS API (문자 발송)
- OpenAI API
- 광주 버스 API

## 개발 도구
- STS
- intelliJ

## 아키텍처
- 계층형 구조 (MVC 패턴)
- RESTful API 설계
- AOP 기반 로깅 및 모니터링
- 이벤트 기반 시스템 통합
