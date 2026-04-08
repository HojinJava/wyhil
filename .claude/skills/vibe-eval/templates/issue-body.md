## 마일스톤
[{MILESTONE_TITLE}]({MILESTONE_URL})

## 담당 모델
{MODEL}

---

## 공통 프롬프트

> 아래 내용이 이번 바이브코딩의 작업 지시입니다. 추가 맥락은 [기능 정의서]({FEATURE_DEF_URL})를 참고하세요.

{COMMON_PROMPT}

---

## 작업 프로세스

1. 브랜치 생성: `issue/{ISSUE_NUMBER}-{MODEL_FILENAME}-{SLUG}`
2. 위 공통 프롬프트 기준으로 바이브코딩 진행
3. 변경 사항 커밋 & 푸시
4. `vibe-sessions/{SLUG}/{MODEL_FILENAME}.md` 파일 작성
   - 레포에 템플릿이 미리 생성되어 있음 — 내용만 채우면 됨
   - 토큰 수 확인 불가 시 `N/A` 로 기입
5. PR 생성 후 이 이슈 연결 (`closes #{ISSUE_NUMBER}`)

---

## 완료 조건
- [ ] 바이브코딩 완료
- [ ] `vibe-sessions/{SLUG}/{MODEL_FILENAME}.md` 작성 후 커밋
- [ ] PR 생성 후 이 Issue 연결
