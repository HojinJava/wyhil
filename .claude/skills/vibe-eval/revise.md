# /vibe-eval revise

기존 마일스톤의 수정 버전을 생성하고 기존 Issue에 대체 안내 코멘트를 추가한다.

## 트리거

사용자가 `/vibe-eval revise {milestone-slug}` 를 실행할 때.

## 사전 확인

1. REPO = `gh repo view --json nameWithOwner -q .nameWithOwner`
2. SLUG는 사용자 입력 인수에서 직접 사용
3. 기존 마일스톤 조회 (제목에 SLUG가 포함된 마일스톤):
```bash
gh api repos/{OWNER}/{REPO}/milestones \
  --jq ".[] | select(.title | contains(\"{SLUG}\"))"
```
- 결과가 없으면: "❌ 슬러그 '{SLUG}'에 해당하는 마일스톤을 찾을 수 없습니다." 출력 후 종료
- 결과가 여러 개면 (버전이 이미 있는 경우): 버전 접미사가 없는 가장 기본 제목부터 조회, 또는 가장 최신 버전 선택

## 실행 절차

### 1단계: 기존 마일스톤 정보 파악

기존 마일스톤 제목에서 파싱:
- OLD_TITLE: 기존 마일스톤 전체 제목 (예: `mall/login-feature [L2]`)
- BASE_NAME: LEVEL과 버전 접미사 모두 제거 (예: `mall/login-feature`)
- LEVEL: `[L{n}]`에서 추출 (예: `2`)
- OLD_SLUG: OLD_TITLE에서 ` [L{n}]` 제거 후 `/`를 `-`로 치환

버전 계산:
```bash
gh api repos/{OWNER}/{REPO}/milestones \
  --jq "[.[] | select(.title | contains(\"{BASE_NAME}\"))] | length"
```
- 조회된 수 + 1 = NEW_VERSION (처음 수정이면 2)

NEW_TITLE = `{BASE_NAME}-v{NEW_VERSION} [L{LEVEL}]`
NEW_SLUG = NEW_TITLE에서 ` [L{n}]` 제거 후 `/`를 `-`로 치환

### 2단계: 수정 이유 입력받기

사용자에게 묻는다:

"수정 이유를 입력해주세요 (기존 마일스톤 Issue에 코멘트로 추가됩니다):"

입력받은 내용을 REVISION_REASON으로 저장.

### 3단계: 새 마일스톤 생성

기존 feature-definitions 파일에서 COMMON_PROMPT와 MODELS를 읽는다:
```bash
cat feature-definitions/{OLD_SLUG}.md
```

새 마일스톤 생성:
```bash
gh api repos/{OWNER}/{REPO}/milestones \
  -X POST \
  -f title="{NEW_TITLE}" \
  -f description="{COMMON_PROMPT}"
```
NEW_MILESTONE_NUMBER 저장.
NEW_MILESTONE_URL = `https://github.com/{OWNER}/{REPO}/milestone/{NEW_MILESTONE_NUMBER}`

### 4단계: 새 모델별 Issue 생성

기존 feature-definitions에서 파싱한 MODELS 목록을 사용.

각 모델에 대해 Issue 생성 (create 스킬의 4단계와 동일한 방식):
- Issue 제목: `[{MODEL}] {NEW_TITLE}`
- 마일스톤: NEW_TITLE
- 세션 파일 경로: `vibe-sessions/{NEW_SLUG}/{MODEL_FILENAME}.md`
- Issue 본문: `skills/vibe-eval/templates/issue-body.md`의 플레이스홀더를 치환

```bash
gh issue create \
  --title "[{MODEL}] {NEW_TITLE}" \
  --body "{ISSUE_BODY}" \
  --milestone "{NEW_TITLE}" \
  --label "eval" \
  --label "project:{PROJECT}" \
  --label "model:{MODEL}"
```

세션 MD 템플릿도 `vibe-sessions/{NEW_SLUG}/` 에 생성.

### 5단계: 기존 Issue 전체에 코멘트 추가

기존 마일스톤의 모든 Issue 목록 조회:
```bash
gh issue list \
  --milestone "{OLD_TITLE}" \
  --label "eval" \
  --json number \
  --jq '.[].number'
```

각 Issue에 코멘트 추가:
```bash
gh issue comment {ISSUE_NUMBER} --body "⚠️ 이 마일스톤은 [{NEW_TITLE}]({NEW_MILESTONE_URL})으로 대체되었습니다.

**수정 이유:** {REVISION_REASON}

새 마일스톤의 Issue에서 작업을 진행해주세요."
```

> 기존 마일스톤과 Issue는 close하지 않음 — 참고용으로 유지

### 6단계: feature-definitions 저장 및 커밋

```bash
cp feature-definitions/{OLD_SLUG}.md feature-definitions/{NEW_SLUG}.md
git add feature-definitions/{NEW_SLUG}.md vibe-sessions/{NEW_SLUG}/
git commit -m "chore: init vibe-eval revision {NEW_TITLE}"
```

### 7단계: 결과 요약 출력

```
✅ 마일스톤 수정 버전 생성 완료

기존: {OLD_TITLE} (#{OLD_MILESTONE_NUMBER})
신규: {NEW_TITLE} ({NEW_MILESTONE_URL})
수정 이유: {REVISION_REASON}

기존 Issue {N}개에 대체 안내 코멘트 추가됨.

새 Issue:
  [{MODEL_1}] → #{NEW_ISSUE_1}  브랜치 제안: issue/{NEW_ISSUE_1}-{MODEL_1_FILENAME}-{NEW_SLUG}
  [{MODEL_2}] → #{NEW_ISSUE_2}  브랜치 제안: issue/{NEW_ISSUE_2}-{MODEL_2_FILENAME}-{NEW_SLUG}
  ...
```
