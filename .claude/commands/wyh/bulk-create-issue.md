---
name: wyh:bulk-create-issue
description: Use when batch-creating multiple vibe-eval GitHub issues from a JSON file. Reads a list of issue definitions and creates them sequentially.
---

# w-bulk-create-issue

JSON 파일에 정의된 이슈 목록을 순차적으로 처리하여 Vibe Eval 이슈를 일괄 생성한다.

## 사용법

```
/w-bulk-create-issue <JSON_FILE_PATH>
```

예시:
```
/w-bulk-create-issue issues.json
/w-bulk-create-issue .claude/bulk/sprint-1.json
```

---

## JSON 포맷

```json
[
  {
    "project": "mall",
    "title": "login-feature",
    "level": "L1",
    "prompt": "AI 모델에게 전달할 공통 프롬프트 내용",
    "models": ["claude", "wyhill", "antigravity"]
  },
  {
    "project": "socket",
    "title": "connection-status",
    "level": "L2",
    "prompt": "AI 모델에게 전달할 공통 프롬프트 내용",
    "models": "all"
  }
]
```

| 필드 | 타입 | 설명 | 허용값 |
|------|------|------|--------|
| `project` | string | 프로젝트 alias | vibe-projects.json에 등록된 alias |
| `title` | string | 기능 제목 (영어, kebab-case 권장) | 예: `login-feature`, `product-review` |
| `level` | string | 난이도 레벨 | `L1`, `L2`, `L3` |
| `prompt` | string | AI에게 전달할 공통 프롬프트 | 자유 텍스트 (여러 줄 가능) |
| `models` | string\|array | 참여 모델 | 모델 키 배열 또는 `"all"` |

**models 허용값:**
- `"all"` — `.github/vibe-models.json`에 등록된 전체 모델
- 배열 — `["claude", "wyhill", "wyhill-guide", "antigravity", "codex"]` 중 선택

---

## 실행 절차

### 0단계: 인수 확인

인수가 없으면 사용법을 출력하고 종료:
```
사용법: /w-bulk-create-issue <JSON_FILE_PATH>
예시:   /w-bulk-create-issue issues.json
```

### 1단계: JSON 파일 읽기 및 유효성 검사

JSON 파일을 읽어 파싱한다.

각 항목에 대해 아래를 검증한다:
- `project`: `.github/vibe-projects.json`에 존재하는 alias인지
- `title`: 비어있지 않은지
- `level`: `L1`, `L2`, `L3` 중 하나인지
- `prompt`: 비어있지 않은지
- `models`: `"all"` 또는 유효한 모델 키 배열인지

유효성 오류가 있으면 항목 번호와 오류 내용을 출력하고 해당 항목은 건너뛴다.

검증 후 처리 예정 목록을 출력한다:

```
📋 일괄 생성 예정 이슈 목록

총 {N}건 (오류 {E}건 제외)

1. [L1] mall/login-feature → claude, wyhill, antigravity
2. [L2] socket/connection-status → 전체 모델 (5개)
3. [L3] mall/product-review → claude, wyhill

계속 진행할까요? (yes/no)
```

`no` 입력 시 종료.

### 2단계: 공통 변수 준비

```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
```

`.github/vibe-models.json`을 읽어 전체 모델 목록 준비.
`.github/vibe-projects.json`을 읽어 프로젝트별 folder 정보 준비.

### 3단계: 이슈 순차 생성

각 항목에 대해 아래 절차를 순서대로 실행한다.
진행 상황을 출력한다:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[1/3] mall/login-feature [L1]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

#### 변수 계산

```
PROJECT_ALIAS = project 값
TARGET_DIR    = vibe-projects.json에서 PROJECT_ALIAS의 folder 값
TITLE_RAW     = title 값
LEVEL         = level 값
COMMON_PROMPT = prompt 값
TITLE         = "{PROJECT_ALIAS}/{TITLE_RAW} [{LEVEL}]"
TITLE_CLEAN   = "{PROJECT_ALIAS}/{TITLE_RAW}"
SLUG          = TITLE_CLEAN의 '/' → '-' 치환
MODELS        = models가 "all"이면 vibe-models.json 전체 키, 아니면 배열 그대로
```

#### feature.md 작성

`FEATURE_DEF_PATH = .claude/feature-definitions/{SLUG}.md`

`.claude/skills/vibe-eval/templates/issue-body.md` 플레이스홀더 치환 후 저장:
- `{TITLE}` → TITLE
- `{COMMON_PROMPT}` → COMMON_PROMPT
- `{TARGET_DIR}` → TARGET_DIR (없으면 `(미지정)`)
- `{ISSUE_NUMBER}` → `{ISSUE_NUMBER}` (임시)

이미 존재하면 덮어쓴다 (확인 없이).

#### 라벨 생성 (없으면)

```bash
gh label create "eval" --color "0075ca" --description "Vibe eval 이슈" 2>/dev/null || true
gh label create "project:{PROJECT_ALIAS}" --color "e4e669" --description "{PROJECT_ALIAS} 프로젝트" 2>/dev/null || true
```

#### 이슈 생성

```bash
gh issue create \
  --title "{TITLE}" \
  --body-file "{FEATURE_DEF_PATH}" \
  --label "eval" \
  --label "project:{PROJECT_ALIAS}"
```

생성된 ISSUE_NUMBER를 저장한다.

feature.md의 `{ISSUE_NUMBER}` 플레이스홀더를 실제 번호로 치환 후 저장.

이슈 바디를 업데이트된 파일로 교체:
```bash
gh issue edit {ISSUE_NUMBER} --body-file "{FEATURE_DEF_PATH}"
```

#### 모델 브랜치에 issue-{N}.md 배포

각 참여 모델에 대해:

1. `.claude/skills/vibe-eval/templates/issue-task.md`를 읽어 변수 치환:
   - `{MODEL}` → 모델 브랜치 키
   - `{ISSUE_NUMBER}` → 실제 이슈 번호
   - `{SLUG}` → SLUG
   - `{COMMON_PROMPT}` → COMMON_PROMPT
   - `{TARGET_DIR}` → TARGET_DIR
   - `{TITLE}` → TITLE

2. base64 인코딩 후 GitHub API로 push:

```bash
FILE_PATH="task/{TARGET_DIR}/issue-{ISSUE_NUMBER}.md"
SHA=$(gh api "repos/{REPO}/contents/$FILE_PATH?ref={MODEL_BRANCH}" --jq '.sha' 2>/dev/null || echo "")
gh api -X PUT "repos/{REPO}/contents/$FILE_PATH" \
  --field message="chore: add issue-{ISSUE_NUMBER} task file [skip ci]" \
  --field content="$(echo -n '{치환된내용}' | base64)" \
  --field branch="{MODEL_BRANCH}" \
  [--field sha="$SHA" (SHA가 있는 경우만)]
```

TARGET_DIR이 없으면 `task/issue-{ISSUE_NUMBER}.md` 사용.

성공: `  ✅ {MODEL} 브랜치 배포`
실패: `  ⚠️ {MODEL} 브랜치 배포 실패 (계속 진행)`

3. main 브랜치에도 동일하게 배포 (참조용).

#### 세션 파일 생성

`COMBINED_SLUG = {SLUG}-{ISSUE_NUMBER}`

각 모델에 대해 `.claude/vibe-sessions/{COMBINED_SLUG}/{MODEL_KEY}.md` 생성.
`.claude/skills/vibe-eval/templates/session.md` 플레이스홀더 치환.

#### 개별 커밋

```bash
git add .claude/feature-definitions/{SLUG}.md .claude/vibe-sessions/{COMBINED_SLUG}/
git commit -m "chore: init vibe-eval session for {TITLE} #{ISSUE_NUMBER}"
```

#### 개별 결과 출력

```
✅ #{ISSUE_NUMBER} — {TITLE}
   브랜치: vibe/{model}/{slug}-{N} (N개 모델)
   URL: https://github.com/{REPO}/issues/{ISSUE_NUMBER}
```

실패 시:
```
❌ mall/login-feature — 이슈 생성 실패: {오류 메시지}
```

### 4단계: 최종 결과 요약

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 일괄 생성 완료

성공: {S}건
실패: {F}건

생성된 이슈:
- #38 mall/login-feature [L1] → claude, wyhill, antigravity
- #39 socket/connection-status [L2] → 전체 5개 모델
- #40 mall/product-review [L3] → claude, wyhill
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```
