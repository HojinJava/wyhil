# Mall 프로젝트 Codebase 분석

## 프로젝트 개요

Spring Boot + MyBatis 기반의 완성형 전자상거래 플랫폼. 관리자 백오피스 시스템과 고객 쇼핑몰 시스템을 모두 포함하며 Docker 컨테이너 배포를 지원합니다.

---

## 모듈 구조

```
mall (멀티 모듈 Maven 프로젝트)
├── mall-common    - 공용 유틸, API 응답, Redis/Swagger 설정 (15 Java files)
├── mall-mbg       - MyBatis Generator 생성 코드, 매퍼, 엔티티 (230 Java files)
├── mall-security  - Spring Security, JWT 인증/인가 (15 Java files)
├── mall-admin     - 백엔드 관리 시스템 API (154 Java files)
├── mall-search    - Elasticsearch 기반 상품 검색 (12 Java files)
├── mall-portal    - 프론트 쇼핑몰 시스템 API (86 Java files)
└── mall-demo      - 기술 데모 및 테스트 코드 (14 Java files)

총 526 Java 파일
```

### 모듈별 역할

| 모듈 | 역할 | 주요 기술 |
|------|------|---------|
| mall-common | 공용 코드 | API 응답 포맷, Redis, Swagger, AOP 로깅 |
| mall-mbg | 데이터 계층 | MyBatis Mapper, Entity, DB 연동 |
| mall-security | 보안 | JWT, Spring Security, 동적 권한 |
| mall-admin | 관리자 API | 상품/주문/회원/판매/콘텐츠 관리 |
| mall-search | 검색 | Elasticsearch 상품 검색 |
| mall-portal | 고객 API | 홈/상품/주문/결제/회원/장바구니 |
| mall-demo | 테스트 | 프레임워크 기술 검증 |

---

## 기술 스택

### 백엔드

| 분류 | 기술 | 버전 |
|------|------|------|
| 핵심 프레임워크 | Spring Boot | 2.7.5 |
| 보안 | Spring Security + JWT (jjwt) | 0.9.1 |
| ORM | MyBatis + MyBatis Generator | 3.5.10 / 1.4.1 |
| 커넥션 풀 | Druid | 1.2.14 |
| 페이징 | PageHelper | 5.3.2 |
| API 문서 | Swagger (SpringFox) | 3.0.0 |
| 유틸 | Hutool, Lombok | 5.8.9 |
| 스토리지 | Aliyun OSS, MinIO | 2.5.0 / 8.4.5 |
| 결제 | Alipay SDK | 4.38.61 |
| Java | JDK | 1.8 |

### 데이터베이스 / 인프라

| 기술 | 버전 | 용도 |
|------|------|------|
| MySQL | 5.7 | 메인 관계형 DB |
| Redis | 7.0 | 캐싱, 세션 |
| MongoDB | 5.0 | 조회 이력, 비정형 데이터 |
| Elasticsearch | 7.17.3 | 상품 전문 검색 |
| RabbitMQ | 3.10.5 | 비동기 메시지 큐 |
| Nginx | 1.22 | 정적 리소스 서버 |
| Docker | - | 컨테이너 배포 |
| Jenkins | - | CI/CD 자동화 |
| Logstash + Kibana | 7.17.3 | 로그 수집/시각화 |

### 프론트엔드

- **관리자 웹 (mall-admin-web)**: Vue.js + Element UI + Axios
- **고객 모바일 (mall-app-web)**: Vue.js + uni-app (멀티플랫폼)

---

## 아키텍처

### 레이어드 아키텍처

```
Controller (REST API)
    ↓
Service (비즈니스 로직)
    ↓
DAO / Repository (데이터 접근)
    ↓
MySQL / MongoDB / Elasticsearch / Redis
```

### 인증/인가 흐름

```
Request
    → JwtAuthenticationTokenFilter (JWT 검증)
    → DynamicSecurityFilter (동적 권한)
    → DynamicAccessDecisionManager (접근 결정)
    → Controller
```

### 도메인 모델 (mall-mbg)

| 접두사 | 도메인 | 주요 엔티티 |
|--------|--------|------------|
| PMS | 상품 관리 | Product, Category, Brand, SkuStock |
| OMS | 주문 관리 | Order, OrderItem, CartItem, ReturnApply |
| UMS | 회원 관리 | Admin, Member, Role, Resource |
| SMS | 판매 관리 | Coupon, FlashPromotion |
| CMS | 콘텐츠 관리 | Subject, Topic, Help |

---

## 의존성 구조

```
mall-common
    ↑
mall-security ← mall-admin
mall-mbg      ← mall-portal
              ← mall-search
              ← mall-demo
```

---

## 주요 설정

### JWT (mall-admin)
- Header: `Authorization`
- 만료: 604800초 (7일)
- 토큰 접두사: `Bearer `

### Redis 캐시
- 기본 만료: 86400초 (24시간)
- AOP 기반 캐싱 (`RedisCacheAspect`)

### 파일 업로드
- 최대 파일 크기: 10MB
- 지원 스토리지: Aliyun OSS, MinIO

---

## 개발 패턴

- **Service-DAO 패턴**: 비즈니스 로직과 데이터 접근 완전 분리
- **전역 예외 처리**: `GlobalExceptionHandler`로 일관된 에러 응답
- **AOP 로깅**: `WebLogAspect`로 웹 요청 자동 로깅
- **표준 응답 포맷**: `CommonResult<T>`, `CommonPage<T>`
- **환경 분리**: `application-dev.yml` / `application-prod.yml`
