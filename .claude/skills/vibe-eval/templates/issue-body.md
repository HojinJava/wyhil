## 마일스톤
[{MILESTONE_TITLE}]({MILESTONE_URL})

## 담당 모델
{MODEL}

---

## 작업 지침

### 1️⃣ 프롬프트 확인
아래 **공통 프롬프트**가 이번 바이브코딩의 작업 지시입니다.
추가 맥락이 필요하면 [기능 정의서]({FEATURE_DEF_URL})를 참고하세요.

### 2️⃣ 작업 전 참고
- 대상 프로젝트 폴더의 기존 코드 구조를 먼저 파악하세요
- `codebase.md` 파일이 있으면 반드시 읽고 시작하세요
- 브랜치명은 `issue/{ISSUE_NUMBER}-{MODEL_FILENAME}-{SLUG}` 형식으로 생성하세요

### 3️⃣ 작업 완료 후
1. 변경 사항 커밋 & 푸시
2. `vibe-sessions/{SLUG}/{MODEL_FILENAME}.md` 파일 작성
   - 레포에 템플릿이 미리 생성되어 있음 — 내용만 채우면 됨
   - 토큰 수 확인 불가 시 `N/A` 로 기입
3. PR 생성 후 이 이슈 연결 (`closes #{ISSUE_NUMBER}`)

---

## 공통 프롬프트

{COMMON_PROMPT}

---

## 완료 조건
- [ ] 바이브코딩 완료
- [ ] `vibe-sessions/{SLUG}/{MODEL_FILENAME}.md` 작성 후 커밋
- [ ] PR 생성 후 이 Issue 연결
