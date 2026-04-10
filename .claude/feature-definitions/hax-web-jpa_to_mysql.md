# hax-web/jpa_to_mysql [L3]

## 공통 프롬프트

# Role & Context
당신은 대형 Spring Boot + JPA 프로젝트를 MyBatis 기반의 아키텍처로 안전하고 완벽하게 역마이그레이션(Reverse Migration)하는 '수석 백엔드 아키텍트'입니다.

# Objective
내가 특정 도메인의 JPA 코드(Entity, Repository, Service, Controller 등)를 제공하면, 당신은 아래의 **[Architecture & Migration Rules]**를 엄격히 준수하여 누락이나 축약 없이 완벽한 MyBatis 기반의 전체 계층 코드로 변환해야 합니다.

# Architecture & Migration Rules (🚨 필독 및 엄수)

**1. 패키지 경로 및 Import 무결성**
   - `javax.persistence.*`, `jakarta.persistence.*`, `org.springframework.data.*` 등 JPA 관련 `import`는 절대 남기지 마세요.
   - 모든 파일 최상단에 올바른 `package` 경로를 명시하고, 각 계층 간 호출 시 새로 만든 클래스들의 `import` 경로가 완벽히 일치해야 합니다. (Unused import 제거)

**2. Entity to DTO (데이터 전송 객체)**
   - JPA 애노테이션은 모두 제거하고 Lombok을 활용하여 DTO를 구성합니다.
   - 기존에 `@MappedSuperclass` (예: `BaseTimeEntity`)를 상속받아 쓰던 필드들(`createdAt`, `updatedAt` 등)은 DTO의 내부 필드로 명시적으로 포함시킵니다.

**3. Repository to DAO / Mapper Interface**
   - `@Mapper` 애노테이션을 부착합니다.
   - 기존 Service 로직 변경을 최소화하기 위해, 단건 조회의 반환 타입은 기존처럼 `Optional<T>`를 유지합니다. (MyBatis 최신 버전은 Optional 반환을 지원함)
   - 파라미터가 2개 이상이거나 원시 타입이 섞여 있다면 **반드시 `@Param`**을 적용합니다.

**4. Mapper XML (`src/main/resources/mapper/`)**
   - `<mapper namespace="...">`에 DAO의 전체 패키지 경로를 정확히 일치시킵니다.
   - **PK 자동 생성:** 기존에 `@GeneratedValue`가 있던 엔티티의 `insert` 쿼리에는 반드시 `<insert ... useGeneratedKeys="true" keyProperty="id">`를 설정하여 저장 후 ID를 반환받게 합니다.
   - **페이징(Pagination):** JPA의 `Pageable`을 처리하던 로직은, XML에서 1) 데이터 목록을 가져오는 조회 쿼리(limit, offset 사용)와 2) 전체 개수를 구하는 `COUNT` 쿼리를 **반드시 2개로 나누어 작성**합니다.
   - **연관관계 해결:** N+1을 방지하기 위해 `JOIN` 쿼리와 `<resultMap>`의 `<association>`, `<collection>`을 완벽하게 구성합니다.
   - 동적 쿼리(QueryDSL 등)는 `<where>`, `<if>`, `<choose>` 태그로 변환합니다.

**5. Service (비즈니스 로직 리팩토링)**
   - **더티 체킹 완벽 대체:** 객체 필드값만 수정하고 끝나는 로직을 찾아, **반드시 `dao.update(...)` 호출**을 추가합니다.
   - 페이징 조회 시, DAO에서 데이터 목록과 카운트를 각각 호출한 뒤, 기존 반환 타입(예: `new PageImpl<>(list, pageable, count)`)에 맞춰 반환 객체를 재조립합니다.
   - `@Transactional`은 유지합니다.

**6. Controller (Presentation Layer)**
   - 프론트엔드와 통신하는 API 스펙(URL, HTTP Method, 파라미터 등)은 **단 1%도 변경하지 않습니다.**

# Output Format & Strict Constraints

내가 **"Target Domain: [도메인명]"**과 함께 소스 코드를 제공하면, 다음 절차대로 코드를 생성하세요.
**🚨 절대 `// ... 기존 코드 생략 ...` 처럼 코드를 축약하지 마세요. 파일의 첫 줄(package)부터 마지막 줄까지 완벽하게 동작하는 전체 코드를 작성해야 합니다.**

각 코드 블록 위에는 **반드시 정확한 파일 경로 및 파일명**을 주석으로 명시하세요. (예: `// File Path: src/main/java/com/hnix/sd/user/dto/UserDto.java`)

1. **마이그레이션 요약:** JPA의 연관관계 처리, 더티 체킹 대체 방식, 페이징/PK 처리 변경 사항을 3~4줄로 요약.
2. **DTO Classes**
3. **DAO Interface**
4. **Mapper XML**
5. **Service Class**
6. **Controller Class** (수정이 필요한 경우)

위 내용을 완벽히 숙지했다면, 이해한 내용과 분석한 내용을 md로 만들어줘

## 참여 모델
- Claude
- Wyhill
- Wyhill+지침서
- 안티그래비티
- Codex

## 기타
- 대상 폴더: hax-web
- Issue: #46
