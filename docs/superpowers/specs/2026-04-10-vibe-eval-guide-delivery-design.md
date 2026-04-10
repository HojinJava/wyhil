# Vibe Eval — 가이드 전달 방식 개선 설계

**날짜:** 2026-04-10  
**상태:** 승인됨

---

## 배경 및 문제

현재 이슈 바디에 개발 가이드, 리뷰어 가이드, 공통 프롬프트가 모두 혼재되어 있다.

- **문제 1:** 이슈 링크를 AI에게 전달하면 HTML body까지 읽혀 지침 파악 실패
- **문제 2:** Repo가 private으로 전환될 경우 이슈 링크 접근 불가
- **문제 3:** 리뷰어가 다른 PC에서 clone 후 작업하는 구조에서 가이드 전달 방법 부재
- **문제 4:** 사람이 보는 내용과 AI가 실행할 내용이 분리되지 않음

---

## 결정된 설계

### 역할 분리 원칙

| 파일 | 대상 | 목적 |
|------|------|------|
| 이슈 바디 | 사람 | 이번 평가에서 무엇을 만드는지 파악 + issue 파일 경로 안내 |
| `issue-{ISSUE_NUMBER}.md` | AI (개발자) | 브랜치 설정 + 실행할 작업 + 제출 규칙 |
| `review-{ISSUE_NUMBER}.md` | AI (리뷰어) | 어떤 PR 리뷰할지 + 평가 양식 |

**파일 위치:** 각 모델 브랜치의 `{TARGET_DIR}/task/` 폴더 안에 생성  
**동시 이슈 지원:** 이슈번호가 파일명에 포함되어 여러 이슈를 동시에 진행해도 덮어쓰기 없음

```
mall/
  task/
    issue-21.md
    review-21.md
    issue-25.md
    review-25.md
```

---

### 1. 이슈 바디 (사람이 읽는 용도)

개발 가이드와 리뷰어 가이드를 제거하고, 사람이 이번 평가를 파악하는 데 필요한 내용만 남긴다.  
각 모델 브랜치에 생성된 `issue-{ISSUE_NUMBER}.md` 파일 경로를 안내하여, 참여자가 파일을 쉽게 찾을 수 있게 한다.

```markdown
# {TITLE}

작업 지시 파일: `{TARGET_DIR}/task/issue-{ISSUE_NUMBER}.md` (본인 모델 브랜치에서 확인)

---

## 작업 내용

{COMMON_PROMPT}

---

## 평가지표

- 공통 프롬프트 이행 (1~5): 요구사항을 빠짐없이 수행했는가
- 결과물 품질 (1~5): 코드 가독성·구조·예외처리
- 자율 완성도 (1~5): 추가 프롬프트·수동 수정 없이 완료했는가
```

**제거 항목:**
- 시작 전 실행 (브랜치 명령어) → `issue-{ISSUE_NUMBER}.md`로 이동
- 제출 규칙 → `issue-{ISSUE_NUMBER}.md`로 이동
- 리뷰어 가이드 → `review-{ISSUE_NUMBER}.md`로 이동
- "대상 폴더" 레이블 → `issue-{ISSUE_NUMBER}.md` 자연어로 포함

---

### 2. `issue-{ISSUE_NUMBER}.md` (AI 개발자용)

이슈 생성 시 `/wyh:w-create-issue`가 모델별로 생성하여 각 모델 브랜치에 push한다.  
모델별로 브랜치명이 다르게 치환되어 생성된다.  
파일 경로: `{TARGET_DIR}/task/issue-{ISSUE_NUMBER}.md`

```markdown
# {TITLE} — 작업 지시 (#{ISSUE_NUMBER})

## 지금 바로 실행

git fetch origin {MODEL}
git checkout -b vibe/{MODEL}/{SLUG}-{ISSUE_NUMBER} origin/{MODEL}

위 명령 실행 후 {TARGET_DIR} 폴더 안에서 작업을 진행한다.

## 해야 할 작업

{COMMON_PROMPT}

## 반드시 지켜야 할 규칙

작업 완료 후 모든 커밋을 1개로 squash한다.
squash 후 위에서 checkout한 {MODEL} 브랜치를 대상으로 PR을 생성한다.
PR 본문에 `References #{ISSUE_NUMBER}` 를 반드시 포함한다.
PR에 본인 모델 라벨을 추가한다.

PR 올리기 전 Commits 탭에서 본인 커밋이 1개인지 확인한다.
여러 개라면 main에서 브랜치를 딴 것이므로 PR을 닫고 처음부터 다시 시작한다.

## PR 본문 필수 항목

모델명:
소요 시간:
토큰 수: (예: 입력 12,000 / 출력 3,500)
프롬프트 횟수:
추가 프롬프트: (없으면 없음)
수동 수정: (없으면 없음)
지침서: (없으면 없음)
```

---

### 3. `review-{ISSUE_NUMBER}.md` (AI 리뷰어용)

PR 생성(`opened` 이벤트)시 GitHub Actions가 자동으로 생성하여 다른 모델 브랜치에 push한다.  
`reopened`, `synchronize` 등 재트리거 이벤트에서는 실행하지 않는다.  
파일 경로: `{TARGET_DIR}/task/review-{ISSUE_NUMBER}.md`

```markdown
# {TITLE} — 리뷰 지시 (#{ISSUE_NUMBER})

## 리뷰할 PR

PR #{PR_NUMBER} — {PR_AUTHOR_MODEL} 모델 작업물

아래 명령으로 PR 내용을 확인한다.
gh pr view {PR_NUMBER} --repo {OWNER}/{REPO}

## 평가 기준

- 공통 프롬프트 이행 (1~5): 요구사항을 빠짐없이 수행했는가
- 결과물 품질 (1~5): 코드 가독성·구조·예외처리
- 자율 완성도 (1~5): 추가 프롬프트·수동 수정 없이 완료했는가

리뷰는 PR diff만 기준으로 한다. 코드를 로컬에서 실행하지 않는다.

## 제출 방식

평가 완료 후 아래 양식을 PR #{PR_NUMBER} 댓글로 등록한다.

종합 평점: /5
공통 프롬프트 이행 (1~5):
결과물 품질 (1~5):
자율 완성도 (1~5):

잘된 점:
아쉬운 점:

모델명:
소요 시간:
토큰 수: (예: 입력 12,000 / 출력 3,500)
프롬프트 횟수:
지침서: (없으면 없음)
```

---

### 4. `/wyh:w-create-issue` 업데이트

이슈 생성 완료(이슈 번호 확정) 직후, 참여 모델 각각의 브랜치에 `issue-{ISSUE_NUMBER}.md`를 생성하여 push한다.

```
신규 흐름:
프로젝트 선택 → 레벨 선택 → 프롬프트 입력 → 모델 선택
→ 이슈 생성 (이슈번호 확정)
→ 참여 모델별 issue-{N}.md 생성 및 각 모델 브랜치에 push
→ 완료 출력
```

**모델별 치환 변수:**
- `{MODEL}` — 모델 브랜치 키 (예: `claude`, `antigravity`)
- `{ISSUE_NUMBER}` — 생성된 이슈 번호
- `{SLUG}` — feature slug
- `{COMMON_PROMPT}` — 공통 프롬프트 전문
- `{TARGET_DIR}` — 대상 프로젝트 폴더

**push 전략:** 이미 동일 파일이 존재하면 덮어쓴다. 이슈 재생성 시 최신 내용으로 갱신된다.

**push 방법:** 각 모델 브랜치에 직접 push한다. 메인 브랜치를 checkout하지 않고 `git push origin HEAD:{MODEL}` 또는 git worktree를 사용하여 현재 작업 브랜치를 유지한 채로 push한다. push 실패 시 오류 메시지를 출력하고 해당 모델을 건너뛴다(다른 모델 push는 계속 진행).

---

### 5. GitHub Actions 업데이트

`review-{ISSUE_NUMBER}.md` 자동 push는 **별도 워크플로우 파일**(`vibe-review-dispatch.yml`)로 분리한다.  
기존 `vibe-pr-validation.yml`은 `contents: read`로 유지하고, 신규 파일에서만 `contents: write`를 선언하여 권한 범위를 최소화한다.

**트리거:** `vibe/*` 브랜치로 PR `opened` 이벤트만 처리  
**권한:** `contents: write`, `pull-requests: read`

**동작:**
1. PR 브랜치명에서 모델 키 파싱 (`vibe/claude/test-21` → `claude`)
2. PR 브랜치명에서 이슈번호 파싱 (`test-21` → `21`)
3. `vibe-models.json`에서 해당 이슈의 참여 모델 목록 읽기
4. PR 작성 모델을 제외한 나머지 모델 브랜치에 `review-{ISSUE_NUMBER}.md` 생성 및 push
5. push 커밋 메시지에 `[skip ci]` 포함하여 워크플로우 재트리거 방지

**참여 모델 파악:** 각 모델 브랜치에 `issue-{ISSUE_NUMBER}.md`가 존재하는지 확인하여 해당 이슈의 참여 모델로 간주한다.

---

## 전체 흐름

```
[Admin]
이슈 생성 (/wyh:w-create-issue)
 └→ 이슈 바디: 공통 프롬프트 + 평가지표 + issue-{N}.md 경로 안내
 └→ issue-{N}.md: 참여 모델 각 브랜치에 자동 push

[각 모델 — 다른 PC]
git pull → {TARGET_DIR}/task/issue-{N}.md 읽기 → 개발 → PR 생성

[GitHub Actions — PR opened 시]
 └→ PR 유효성 검사 (기존)
 └→ {TARGET_DIR}/task/review-{N}.md 생성 → 나머지 참여 모델 브랜치에 자동 push

[각 리뷰어 모델 — 다른 PC]
git pull → {TARGET_DIR}/task/review-{N}.md 읽기 → PR diff 리뷰 → PR 댓글로 평가서 등록
```
