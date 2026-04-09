# mall - Codebase Documentation

## 1. 프로젝트 개요

`mall` 프로젝트는 완전한 전자상거래(쇼핑몰) 시스템 구축을 목표로 하는 오픈소스 프로젝트입니다. 현재 소프트웨어 개발에서 주류로 사용되는 Java 기술 스택을 적극 활용하여 구축되었으며, 시스템은 고객이 상품을 구매할 수 있는 쇼핑몰 시스템(프론트엔드/API)과 상품 및 주문 정보 등을 관리할 수 있는 관리자 시스템(백오피스)으로 구성되어 있습니다. Docker 및 Docker Compose를 통한 빠른 컨테이너 환경 구축 및 배포를 지원하여 개발 환경 구성과 운영이 용이한 특징이 있습니다.

## 2. 기술 스택 및 의존성

백엔드는 Spring Boot 체계를 기반으로 하며 주요 라이브러리와 프레임워크는 다음과 같습니다. 

### 핵심 프레임워크 
- **Spring Boot 2.7.5**: 전반적인 어플리케이션 기반 프레임워크 
- **Spring Security**: 어플리케이션 인증 및 인가 처리
- **MyBatis 3.5.10**: 데이터베이스 ORM 계층
- **MyBatis Generator 1.4.1**: 데이터베이스 테이블 기반의 데이터 접근 계층 코드 자동 생성 기능 

### 저장소 및 미들웨어
- **MySQL 8.0**: 핵심 비즈니스 데이터 저장관계형 데이터베이스
- **Redis 7.0**: 데이터 캐싱 및 분산 환경의 공유 자원 관리
- **Elasticsearch 7.17**: 상품 검색을 위한 분산 검색 엔진
- **MongoDB 5.0**: 시스템 로그 및 일부 Document 형태의 데이터 보관 (NoSQL)
- **RabbitMQ 3.10**: 서비스 간 비동기 메시지 통신 (메시지 큐)
- **MinIO / Aliyun OSS**: 이미지 및 정적 파일들을 저장하는 분산 객체 스토리지

### 유틸리티 및 기타
- **JWT (jjwt 0.9.1)**: 상태를 유지하지 않는(Stateless) 토큰 기반 인증 제공
- **Hutool 5.8.9**: Java 개발 시 자주 사용하는 유틸리티 통합 라이브러리
- **PageHelper 5.3.2**: MyBatis의 페이징 쿼리를 도와주는 플러그인
- **Druid 1.2.14**: 데이터베이스 커넥션 풀 관리
- **Lombok**: 기본 Java 모델 작성 시 보일러플레이트 코드 최소화
- **Swagger UI**: API 문서 자동화 

## 3. 프로젝트 구조

Java 어플리케이션은 아키텍처 역할 구분을 위해 Maven Multi-Module 구조를 채택하고 있습니다. 

```lua
mall
├── mall-common   -- [공통 모듈] 유틸 클래스, 전역 예외 처리 및 공통 도메인/응답 캡슐화 코드
├── mall-mbg      -- [데이터 모듈] MyBatisGenerator로 생성된 도메인 모델(Entity) 및 매퍼(Mapper)
├── mall-security -- [보안 모듈] SpringSecurity와 JWT를 결합한 인증, 인가 처리 공용 로직
├── mall-admin    -- [백오피스 APi] 상품, 주문, 회원 등 관리자 시스템을 위한 REST API 서버
├── mall-search   -- [검색 API] Elasticsearch를 활용하는 전용 상품 검색 모듈
├── mall-portal   -- [소비자 쇼핑몰 API] 장바구니, 주문, 회원 등 쇼핑몰 구매 고객을 위한 API 
├── mall-demo     -- [데모 모듈] 프로젝트 핵심 설정 및 프레임워크 기술 테스트용 모듈
└── pom.xml       -- 전체 의존성 및 버전 관리를 위한 부모 설정 POM
```

## 4. 핵심 아키텍처

- **Layered 모놀리식 아키텍처**: 모든 핵심 비즈니스 모듈이 하나의 빌드로 구성될 수 있는 형태이지만 역할(역할별 API, 보안, 인프라 모델 등)에 따라 잘 나뉘어져 있습니다. 
- **Headless API 중심**: 프론트엔드 시스템(`mall-admin-web`, `mall-app-web`)과 분리되어 완전히 독립적인 RESTful API를 제공합니다.
- **인증 메커니즘**: 사용자의 로그인 이후 발급된 JWT를 HTTP Header에 첨부하여, `mall-security`내부의 Security Filter Chain을 거치며 인가 여부를 판별하는 Stateless 인증 처리가 핵심입니다.
- **CQRS 기반 검색 성능 개선 고려**: 일반 RDBMS에서 복잡한 쿼리로 진행되는 상품 검색은 Elasticsearch 기반의 `mall-search` 모듈로 분리하여 검색 성능 개선에 초점을 맞춥니다.

## 5. 주요 파일 및 모듈 분석

### `pom.xml` (루트)
부모 POM으로서, `dependencyManagement` 영역에 포함될 모든 버전 번호들을 속성(`<properties>`)으로 일원화 관리합니다. Maven의 Docker 플러그인을 비롯한 빌드 설정도 정의되어 있습니다.

### `mall-admin` & `mall-portal` 모듈
주요 비즈니스 계층입니다. `Controller` 계층으로 클라이언트 요청을 받고, `Service` 계층에서 트랜잭션을 포함한 비즈니스 로직을 처리하며, 데이터 엑세스는 `mall-mbg` 모듈이나 별도로 작성한 커스텀 MyBatis `Dao` 인터페이스에 위임합니다.

### `mall-mbg` 모듈
데이터베이스 테이블 변동이 있을 때 `MyBatisGenerator` 설정 파일을 통해 자동 실행되어 코드가 업데이트 됩니다. 일반적인 단일/단순 CRUD는 이 곳에서 생성된 `Mapper`와 `Example`객체를 활용하여 별도의 XML 작성 없이 빠르게 쿼리를 수행할 수 있는 기반이 됩니다.

## 6. 코드 품질 분석

- **장점**: 
  - 모듈별 역할 분리가 명확하고, 엔터프라이즈 환경에서 검증된 기술들을 학습하고 참조하기에 매우 훌륭한 레퍼런스 프로젝트입니다.
  - MyBatis의 중복성 있는 CRUD를 Generator로 회피하고 동적 쿼리를 적극 활용합니다.
  - 전역적인 API Response 형태와 Exception 처리, Controller 단에서의 AOP 로깅 등 공통 설계가 잘 갖추어져 있습니다.
- **아쉬운 점 / 주의점**: 
  - 의존성 결합도: `mall-admin`과 `mall-portal` 모두 `mall-mbg`, `mall-common`에 크게 결합되어 있어, 추후 MSA 등으로 분리할 시 도메인 분해에 따른 코드 리팩토링이 필요할 수 있습니다.
  - 단위 테스트 코드: 데모 형태나 통합 모듈 테스트는 존재하나 핵심 비즈니스 단위에 대한 순수 단위 테스트(Unit Test) 커버리지가 다소 부족할 수 있습니다.

## 7. 개선 로드맵

1. **최신 프레임워크 환경 마이그레이션**: Spring Boot 3.x, Java 17+ 레코드를 활용한 성능 최적화 (현재 진행 중인 dev-v3 브랜치 참고).
2. **단위 테스트 및 통합 테스트 강화**: Junit5 & Mockito 등을 이용해 주요 결제/주문 계산 등 코어 로직의 무결성 검증 추가.
3. **마이크로서비스화 (MSA)**: `mall-swarm`이라는 분리된 프로젝트가 있으나 단일 모놀리식 안에서도 도메인 주도 설계(DDD)를 채택하여 Bounded Context를 더 명확히 개선해 나가는 것을 목표로 함.

## 8. 개발 가이드

1. **의존성 구동**: 프로젝트 로컬 구동 전, `MySQL`, `Redis`, `RabbitMQ` 등 필수 인프라는 시스템에 설치되어 있거나 Docker Container를 통해 미리 동작하고 있어야 합니다.
2. **코드 생성 규칙**: 테이블 구조가 변경되면, `MyBatisGenerator`를 다시 실행해 `mall-mbg` 모듈을 재구성해야 합니다. 이후 기존 엔티티 클래스에 미칠 영향을 검토 후 진행하세요.
3. **권한 처리**: 컨트롤러에서 권한이나 역할을 체크해야 한다면 Spring Security의 어노테이션이나 `mall-security` 설정 파트를 확인하여 정책을 수립하세요.

## 9. AI 어시스턴트 참고 섹션

- **멀티 모듈 컨텍스트 파악 필수**: 코드를 파악하거나 수정할 때, 인터페이스가 `mall-mbg` 혹은 `mall-common`에 있고 해당 구현체가 비즈니스 모듈(예: `mall-admin`)에 있는 상황을 항상 염두에 두어야 효율적인 코드 검색 및 수정이 가능합니다.
- **MyBatis Generator 우회**: 자동 생성되는 `mbg` 파일을 직접 수정하는 것은 지양해야 합니다. 복잡한 쿼리나 조인은 각 어플리케이션의 `dao` 패키지(예: `mall-admin/src/main/java/com/macro/mall/dao`) 하위에 별도의 커스텀 인터페이스 및 `src/main/resources/dao` 안의 XML 파일로 분리시켜 작성하세요.
- **DTO와 Entity의 구분**: 데이터베이스 모델인 Entity 객체는 응답값이나 요청값으로 곧바로 사용하지 말고 커스텀 DTO(혹은 Param) 객체를 만들어 `BeanUtils` 복사 등을 통해 컨트롤러 계층으로 변환, 반환하는 방식으로 코드를 제안하세요.
- **보안 설정 추가 가이드**: 새로운 API 엔드포인트가 인증 없이 접속 가능해야 한다면 `mall-security` 모듈 내의 인가 무시 설정(Ignore URL Property List) 파트를 함께 찾아볼 수 있도록 설계해야 합니다.
