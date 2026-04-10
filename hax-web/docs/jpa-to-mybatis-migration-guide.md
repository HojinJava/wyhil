# JPA → MyBatis 역마이그레이션 분석 가이드

## 1. 마이그레이션 규칙 숙지 요약

### 1-1. hax-web 프로젝트 현황

`hax-web`은 `mybatis-spring-boot-starter 3.0.3` + `MariaDB`를 사용하는 순수 MyBatis 기반 프로젝트입니다.
JPA/Hibernate 의존성은 `pom.xml`에 존재하지 않으며, 모든 데이터 접근은 `@Mapper` DAO 인터페이스와 `resources/mapper/*.xml`을 통해 이루어집니다.

---

## 2. 계층별 마이그레이션 규칙 분석

### 2-1. Import 무결성 규칙

JPA 코드를 받았을 때 반드시 제거해야 할 import:

```java
// 제거 대상
import javax.persistence.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;  // Service 계층에서는 유지 가능
```

추가해야 할 import:

```java
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;
import java.util.List;
```

hax-web 적용 예시 (`UserDao.java`):

```java
package com.hnix.sd.common.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;
import java.util.List;

@Mapper
public interface UserDao { ... }
```

---

### 2-2. Entity → DTO 변환 규칙

JPA Entity 예시:

```java
@Entity
@Table(name = "tc_user")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_nm")
    private String userNm;

    // BaseTimeEntity 상속 필드: createdAt, updatedAt
}
```

MyBatis DTO 변환 결과:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private String userId;
    private String userNm;
    // BaseTimeEntity 상속 필드를 명시적으로 포함
    private LocalDateTime regDt;   // createdAt 대응
    private LocalDateTime modDt;   // updatedAt 대응
}
```

핵심 변환 원칙:
- `@Entity`, `@Table`, `@Column`, `@Id`, `@GeneratedValue` 애노테이션 전체 제거
- `@MappedSuperclass`로 상속받던 `createdAt`/`updatedAt` 필드를 DTO 내부 필드로 명시
- Lombok `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` 적용

---

### 2-3. Repository → DAO / Mapper Interface 변환 규칙

JPA Repository 예시:

```java
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserEmail(String userEmail);
    List<User> findAllByDeleteYn(char deleteYn);
    Page<User> findByUserNmContaining(String keyword, Pageable pageable);
}
```

MyBatis DAO 변환 결과:

```java
@Mapper
public interface UserDao {

    // 단건 조회 - Optional 유지 (default 메서드로 래핑)
    UserDto findByUserIdEntity(@Param("userId") String userId);

    default Optional<UserDto> findByUserId(String userId) {
        return Optional.ofNullable(findByUserIdEntity(userId));
    }

    // 파라미터 2개 이상 시 반드시 @Param 적용
    UserDto findByUserCheckCdAndDeleteYnEntity(
        @Param("userCheckCd") String userCheckCd,
        @Param("deleteYn") char deleteYn
    );

    default Optional<UserDto> findByUserCheckCdAndDeleteYn(String userCheckCd, char deleteYn) {
        return Optional.ofNullable(findByUserCheckCdAndDeleteYnEntity(userCheckCd, deleteYn));
    }

    // 페이징: 목록 쿼리 + COUNT 쿼리를 2개로 분리
    List<UserGridDto> findUserGridPagination(UserSearchPageDto searchDto);
    long countUserGridPagination(UserSearchPageDto searchDto);

    // INSERT / UPDATE 분리
    void insertUser(UserDto user);
    void updateUser(UserDto user);

    // save() 패턴: default 메서드로 구현
    default UserDto save(UserDto user) {
        if (findByUserIdEntity(user.getUserId()) == null) {
            insertUser(user);
        } else {
            updateUser(user);
        }
        return user;
    }
}
```

핵심 변환 원칙:
- `@Mapper` 애노테이션 필수
- `Optional<T>` 반환은 `default` 메서드로 래핑하여 유지
- 파라미터 2개 이상 또는 원시 타입 혼합 시 `@Param` 필수
- `Page<T>` 반환 메서드는 목록 쿼리 + COUNT 쿼리 2개로 분리

---

### 2-4. Mapper XML 작성 규칙

파일 위치: `src/main/resources/mapper/`

기본 구조:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.hnix.sd.common.user.dao.UserDao">

    <!-- PK 자동 생성: @GeneratedValue 대응 -->
    <insert id="insertUser" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO tc_user (user_id, user_nm, user_email, dept_cd, reg_dt)
        VALUES (#{userId}, #{userNm}, #{userEmail}, #{deptCd}, NOW())
    </insert>

    <!-- UPDATE: 더티 체킹 대체 -->
    <update id="updateUser">
        UPDATE tc_user
        SET user_nm       = #{userNm},
            user_email    = #{userEmail},
            dept_cd       = #{deptCd},
            mod_dt        = NOW()
        WHERE user_id     = #{userId}
    </update>

    <!-- 페이징: 목록 쿼리 -->
    <select id="findUserGridPagination" resultType="com.hnix.sd.common.user.dto.UserGridDto">
        SELECT user_id, user_nm, user_email, dept_cd
        FROM tc_user
        <where>
            <if test="keyword != null and keyword != ''">
                AND (user_nm LIKE CONCAT('%', #{keyword}, '%')
                  OR user_email LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="deleteYn != null">
                AND delete_yn = #{deleteYn}
            </if>
        </where>
        ORDER BY reg_dt DESC
        LIMIT #{pageSize} OFFSET #{offset}
    </select>

    <!-- 페이징: COUNT 쿼리 -->
    <select id="countUserGridPagination" resultType="long">
        SELECT COUNT(*)
        FROM tc_user
        <where>
            <if test="keyword != null and keyword != ''">
                AND (user_nm LIKE CONCAT('%', #{keyword}, '%')
                  OR user_email LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="deleteYn != null">
                AND delete_yn = #{deleteYn}
            </if>
        </where>
    </select>

    <!-- JOIN + resultMap: N+1 방지 -->
    <resultMap id="userWithDeptMap" type="com.hnix.sd.common.user.dto.UserDetailDto">
        <id     property="userId"         column="user_id"/>
        <result property="userNm"         column="user_nm"/>
        <result property="userEmail"      column="user_email"/>
        <association property="deptInfo" javaType="com.hnix.sd.common.department.dto.DepartmentDto">
            <result property="deptNm"     column="dept_nm"/>
            <result property="companyNm"  column="company_nm"/>
        </association>
    </resultMap>

    <select id="findByUserWithDeptEntity" resultMap="userWithDeptMap">
        SELECT u.user_id, u.user_nm, u.user_email,
               d.dept_nm, d.company_nm
        FROM tc_user u
        LEFT JOIN tc_department d ON u.dept_cd = d.dept_cd
        WHERE u.user_id = #{userId}
    </select>

    <!-- 동적 쿼리: QueryDSL → MyBatis 변환 -->
    <select id="findAllUserGrid" resultType="com.hnix.sd.common.user.dto.UserGridDto">
        SELECT user_id, user_nm, user_email, dept_cd, delete_yn
        FROM tc_user
        <where>
            <choose>
                <when test="searchType == 'name'">
                    AND user_nm LIKE CONCAT('%', #{keyword}, '%')
                </when>
                <when test="searchType == 'email'">
                    AND user_email LIKE CONCAT('%', #{keyword}, '%')
                </when>
                <otherwise>
                    <if test="keyword != null and keyword != ''">
                        AND (user_nm LIKE CONCAT('%', #{keyword}, '%')
                          OR user_email LIKE CONCAT('%', #{keyword}, '%'))
                    </if>
                </otherwise>
            </choose>
        </where>
        ORDER BY reg_dt DESC
    </select>

</mapper>
```

핵심 작성 원칙:
- `namespace`는 DAO 인터페이스의 전체 패키지 경로와 정확히 일치
- `@GeneratedValue` 대응: `useGeneratedKeys="true" keyProperty="pk컬럼"`
- 페이징: `LIMIT #{pageSize} OFFSET #{offset}` — 목록/COUNT 쿼리 2개로 분리
- N+1 방지: `JOIN` + `<resultMap>` + `<association>` / `<collection>` 조합
- 동적 쿼리: `<where>`, `<if>`, `<choose>`, `<when>`, `<otherwise>` 태그 활용

---

### 2-5. Service 계층 리팩토링 규칙

더티 체킹 대체 패턴:

```java
// JPA (더티 체킹 방식)
@Transactional
public void updateUserName(String userId, String newName) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BizException("USER_NOT_FOUND"));
    user.setUserNm(newName);  // 더티 체킹으로 자동 UPDATE
}

// MyBatis (명시적 UPDATE 호출 필수)
@Transactional
public void updateUserName(String userId, String newName) {
    UserDto user = userDao.findByUserId(userId)
        .orElseThrow(() -> new BizException("USER_NOT_FOUND"));
    user.setUserNm(newName);
    userDao.updateUser(user);  // 반드시 명시적 UPDATE 호출
}
```

페이징 재조립 패턴:

```java
// JPA
public Page<UserGridDto> getUserGrid(UserSearchPageDto searchDto, Pageable pageable) {
    return userRepository.findByUserNmContaining(searchDto.getKeyword(), pageable);
}

// MyBatis
public Page<UserGridDto> getUserGrid(UserSearchPageDto searchDto, Pageable pageable) {
    List<UserGridDto> list = userDao.findUserGridPagination(searchDto);
    long total = userDao.countUserGridPagination(searchDto);
    return new PageImpl<>(list, pageable, total);  // PageImpl 재조립
}
```

핵심 리팩토링 원칙:
- 더티 체킹 로직 → 반드시 `dao.update(...)` 명시 호출로 대체
- `Page<T>` 반환 → `new PageImpl<>(list, pageable, count)` 재조립
- `@Transactional` 애노테이션 유지

---

### 2-6. Controller 계층 규칙

Controller는 API 스펙(URL, HTTP Method, 요청/응답 파라미터)을 1%도 변경하지 않습니다.

```java
// JPA와 MyBatis 모두 동일한 API 스펙 유지
@GetMapping("/users/{userId}")
public ResponseEntity<ApiResponse<UserDetailDto>> getUser(@PathVariable String userId) {
    return ResponseEntity.ok(ApiResponse.success(userService.getUserDetail(userId)));
}
```

---

## 3. hax-web 프로젝트 참조 구조

### 3-1. 패키지 구조

```
hax-web/src/main/java/com/hnix/sd/
├── common/
│   ├── user/
│   │   ├── dao/UserDao.java          ← @Mapper 인터페이스
│   │   ├── dto/UserDto.java          ← Lombok DTO (JPA 애노테이션 없음)
│   │   ├── service/UserService.java  ← @Transactional, 명시적 update 호출
│   │   └── UserController.java       ← API 스펙 불변
│   ├── department/
│   ├── code/
│   └── ...
├── work/
│   ├── software/
│   └── registration/
└── spring/config/MybatisConfig.java  ← MyBatis 설정
```

### 3-2. Mapper XML 구조

```
hax-web/src/main/resources/mapper/
├── common/
│   ├── UserMapper.xml
│   ├── CodeMapper.xml
│   ├── MenuMapper.xml
│   └── department/DepartmentMapper.xml
├── work/
│   ├── contract.xml
│   ├── workRegistration.xml
│   └── ...
└── mybatis-config.xml
```

---

## 4. 마이그레이션 체크리스트

도메인별 JPA 코드를 받으면 다음 순서로 진행합니다.

### Step 1. 사전 분석
- [ ] 대상 Entity의 JPA 연관관계 파악 (`@OneToMany`, `@ManyToOne`, `@ManyToMany`)
- [ ] `@GeneratedValue` 적용 여부 확인 (Mapper XML에서 `useGeneratedKeys` 설정 필요)
- [ ] `Pageable` 파라미터 사용 여부 확인 (목록/COUNT 쿼리 분리 필요)
- [ ] 더티 체킹 로직 위치 파악 (Service에서 명시적 UPDATE로 대체 필요)

### Step 2. 코드 생성 순서
1. DTO 클래스 (Entity 변환, JPA 애노테이션 제거, BaseTimeEntity 필드 명시화)
2. DAO 인터페이스 (@Mapper 적용, Optional default 래핑, @Param 적용)
3. Mapper XML (namespace 일치, useGeneratedKeys, 페이징, JOIN+resultMap)
4. Service 클래스 (더티 체킹 → 명시적 update, 페이징 재조립)
5. Controller 클래스 (API 스펙 불변 확인 후 필요 시 최소 수정)

### Step 3. 검증
- [ ] `javax.persistence.*`, `jakarta.persistence.*` import 잔존 없음
- [ ] `org.springframework.data.*` import 잔존 없음 (PageImpl 제외)
- [ ] 더티 체킹 로직에 명시적 `dao.update(...)` 추가됨
- [ ] 페이징 쿼리가 목록/COUNT 2개로 분리됨
- [ ] Mapper XML의 `namespace`가 DAO 전체 경로와 일치
- [ ] `@Param` 적용 (파라미터 2개 이상, 원시 타입 혼합 시)

---

## 5. 마이그레이션 요약 (3줄)

JPA의 `@OneToMany`/`@ManyToOne` 연관관계는 Mapper XML의 `<association>` + `<collection>` + `JOIN` 쿼리로 대체하여 N+1 문제를 해소합니다.
더티 체킹(필드값만 수정하고 끝나는 로직)은 Service 계층에서 반드시 `dao.updateXxx(dto)` 명시 호출로 대체합니다.
페이징은 `Pageable` 대신 `DTO`로 파라미터를 받아 목록 쿼리(`LIMIT/OFFSET`)와 COUNT 쿼리 2개를 분리 호출한 뒤 `new PageImpl<>(list, pageable, total)`로 재조립합니다.
