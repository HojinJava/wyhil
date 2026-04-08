## 공통 프롬프트

[feature-definitions/{SLUG}.md]({FEATURE_DEF_URL})

---

## 참여 모델 & 작업 규칙

이슈를 확인한 후 본인 모델에 해당하는 브랜치를 생성하고 PR을 제출하세요.  
추가 프롬프트 없이 공통 프롬프트만으로 진행합니다.

| 모델 | 브랜치 | PR 제목 |
|------|--------|---------|
{MODEL_RULES_TABLE}

---

## 작업 프로세스 (모델 공통)

1. 위 표에서 본인 브랜치 생성 후 바이브코딩 진행
2. 변경사항 커밋 & 푸시
3. `vibe-sessions/{COMBINED_SLUG}/{본인_모델_파일명}.md` 작성 후 커밋
4. PR 생성 — 제목과 브랜치명은 위 표 참조, PR 본문에 `References #{ISSUE_NUMBER}` 포함

> ⚠️ `Closes` 대신 `References` 사용 — 이슈는 모든 모델 완료 후 관리자가 수동으로 닫습니다.

---

## 완료 체크리스트

{MODEL_CHECKLIST}
