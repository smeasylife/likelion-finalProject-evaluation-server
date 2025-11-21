# 멋사 13기 프로젝트 최종 발표 채점 시스템 - 백엔드 명세서

## 1. 시스템 개요
본 백엔드 시스템은 멋사 13기 4개 팀의 최종 발표 채점을 위한 API 서비스입니다. 심사위원과 아기사자들이 각 팀을 15개 항목(디자인 5개, 개발 7개, 공통 3개)에 대해 1-5점으로 평가하며, 평가 결과를 집계하여 순위를 산출합니다.

## 2. 기술 스택
- **언어**: Java 17+
- **프레임워크**: Spring Boot 3.x
- **데이터베이스**: JPA/Hibernate
- **예외 처리**: GlobalExceptionHandler

## 3. 데이터베이스 설계

### 3.1 Team Entity
```java
@Entity
public class Team {
    private Long id;
    private String name;          // 팀 이름 (unique)
    private String description;   // 팀 설명
    private List<Evaluation> evaluations; // 평가 목록 (@JsonIgnore)
}
```

### 3.2 Evaluation Entity
```java
@Entity
public class Evaluation {
    private Long id;
    private Team team;                    // 참조 팀 (Many-to-One)
    private String evaluatorRole;         // 평가자 역할 ("심사위원", "아기사자")
    private String evaluatorName;         // 평가자 이름
    private Integer designTotalScore;     // 디자인 총점 (5-25점)
    private Integer developmentTotalScore; // 개발 총점 (7-35점)
    private Integer commonTotalScore;     // 공통 총점 (3-15점)
}
```

### 3.3 제약 조건
- **Unique Constraint**: (evaluator_role, evaluator_name, team_id) 조합으로 중복 평가 방지
- **Foreign Key**: evaluation.team_id → teams.id

## 4. API 엔드포인트

### 4.1 POST /api/evaluations - 평가 제출
- **설명**: 프론트엔드 handleSubmit 함수와 연동
- **Request Body**:
```json
{
  "teamName": "A팀",
  "evaluatorRole": "심사위원",
  "evaluatorName": "홍길동",
  "designTotalScore": 20,
  "developmentTotalScore": 28,
  "commonTotalScore": 12
}
```
- **Response**:
```json
{
  "id": 1,
  "teamName": "A팀",
  "evaluatorRole": "심사위원",
  "evaluatorName": "홍길동",
  "designTotalScore": 20,
  "developmentTotalScore": 28,
  "commonTotalScore": 12
}
```

### 4.2 GET /api/results - 전체 결과 조회
- **설명**: ResultPage.jsx의 결과 조회와 연동
- **Response**:
```json
[
  {
    "teamId": 1,
    "teamName": "A팀",
    "judgeTotalScore": 85,
    "judgeEvaluationCount": 3,
    "menteeTotalScore": 120,
    "menteeEvaluationCount": 5
  }
]
```

## 5. 핵심 기능

### 5.1 중복 제출 방지
- 동일한 평가자(evaluator_name, evaluator_role)가 동일 팀 재평가 불가
- DuplicateEvaluationException 발생 시 HTTP 409 Conflict 응답

### 5.2 점수 검증
- 디자인 점수: 5-25점
- 개발 점수: 7-35점
- 공통 점수: 3-15점
- 범위 벗어날 시 IllegalArgumentException 발생

### 5.3 평가자 역할별 집계
- 심사위원 평가: judgeTotalScore, judgeEvaluationCount
- 아기사자 평가: menteeTotalScore, menteeEvaluationCount
- 각 역할별로 독립적인 점수 집계 및 카운팅

### 5.4 예외 처리
- **GlobalExceptionHandler**: 모든 예외를 중앙에서 처리
- **DuplicateEvaluationException**: HTTP 409 Conflict
- **IllegalArgumentException**: HTTP 400 Bad Request
- **MethodArgumentNotValidException**: HTTP 400 Bad Request
- **Exception**: HTTP 500 Internal Server Error

### 5.5 CORS 설정
- WebConfig를 통한 전역 CORS 설정
- allowedOriginPatterns("*")로 유연한 도메인 허용

## 6. DTO 구조

### 6.1 EvaluationRequest
- 평가 제출 요청 DTO
- 모든 필드 검증 포함

### 6.2 EvaluationResponse
- 평가 제출 응답 DTO
- 순환 참조 방지를 위해 Entity 대신 DTO 사용

### 6.3 TeamResult
- 팀 결과 응답 DTO
- 심사위원/아기사자 역할별 점수 분리 제공
