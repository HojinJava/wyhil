---
name: w-create-issue
description: Use when creating a new vibe-eval GitHub issue for this project. Collects feature prompt information step by step and creates the issue automatically.
---

# w-create-issue

이 프로젝트 전용 스킬. Vibe Eval 이슈 생성에 필요한 정보를 순차적으로 입력받아 feature.md를 작성하고 GitHub Issue를 생성한다.

## 실행 절차

아래 질문을 **하나씩** 순서대로 묻는다. 이전 답변을 받은 후 다음 질문으로 넘어간다.

### 질문 순서

**Q1. 기능 제목**
"기능/작업 이름을 입력하세요. (예: `mall/login-feature`, `mall/product-review`)"

- 입력값을 TITLE_RAW로 저장
- 형식: `{project}/{feature-name}` 권장 (슬래시 포함 가능)

**Q2. 프롬프트 레벨**
"난이도 레벨을 선택하세요: L1 (단순) / L2 (중간) / L3 (복잡)"

- 입력값을 LEVEL로 저장 (L1 / L2 / L3)
- TITLE = `{TITLE_RAW} [{LEVEL}]`

**Q3. 공통 프롬프트**
"AI 모델에게 전달할 공통 프롬프트를 입력하세요. (여러 줄 가능, 입력 완료 후 알려주세요)"

- 입력값을 COMMON_PROMPT로 저장

**Q4. 대상 폴더**
"작업 대상 폴더를 입력하세요. (예: `mall/`, `src/`, 없으면 Enter)"

- 입력값을 TARGET_DIR로 저장 (없으면 생략)

**Q5. 참여 모델**
현재 레포에 등록된 모델 라벨 목록을 보여준다:
```
gh label list --repo $(gh repo view --json nameWithOwner -q .nameWithOwner)
```
에서 `model:` 접두사 라벨을 필터링해 목록 표시.

"참여 모델을 선택하세요 (쉼표로 구분, 예: `Claude, Wyhill, 안티그래비티`). 전체 선택은 `전부`"

- 입력값을 MODELS 리스트로 저장
- `전부` 입력 시 model: 라벨 전체 선택

**Q6. 확인**
수집한 정보를 요약해서 보여주고 확인을 요청한다:

```
📋 생성 예정 이슈 요약

제목:   {TITLE}
레벨:   {LEVEL}
폴더:   {TARGET_DIR}
모델:   {MODELS 목록}

프롬프트:
{COMMON_PROMPT}

이대로 진행할까요? (yes / 수정할 항목 번호)
```

수정 요청 시 해당 질문만 다시 묻는다.

---

## Feature 파일 생성 및 이슈 생성

확인 후 다음 절차를 자동 실행한다.

### 1단계: 변수 계산

```
TITLE_CLEAN = TITLE에서 ' [L{n}]' 제거  (예: mall/login-feature)
SLUG = TITLE_CLEAN의 '/' → '-' 치환     (예: mall-login-feature)
PROJECT = SLUG의 첫 번째 '-' 앞 부분    (예: mall)
REPO = gh repo view --json nameWithOwner -q .nameWithOwner
```

모델별 SLUG 변환 규칙:
| 모델명 | MODEL_SLUG |
|--------|-----------|
| Claude | claude |
| Wyhill | wyhill |
| Wyhill+지침서 | wyhill-guide |
| Cortex Code | cortex-code |
| 안티그래비티 | antigravity |
| 코덱스 코드 | cortex-code |

### 2단계: feature.md 작성

`feature-definitions/{SLUG}.md` 파일 생성:

```markdown
# {TITLE}

## 대상 폴더
{TARGET_DIR}

## 공통 프롬프트
{COMMON_PROMPT}

## 참여 모델
{MODELS 각 항목을 "- 모델명" 형식으로}
```

이미 존재하면 덮어쓰기 전 확인.

### 3단계: 라벨 생성 (없으면)

```bash
gh label create "eval" --color "0075ca" --description "Vibe eval 이슈" 2>/dev/null || true
gh label create "project:{PROJECT}" --color "e4e669" --description "{PROJECT} 프로젝트" 2>/dev/null || true
```

### 4단계: 이슈 본문 구성

`FEATURE_DEF_URL = https://github.com/{REPO}/blob/main/feature-definitions/{SLUG}.md`

MODEL_RULES_TABLE (ISSUE_NUMBER는 플레이스홀더로 먼저 `{ISSUE_NUMBER}` 사용):
```
| {MODEL} | `vibe/{MODEL_SLUG}/{SLUG}-{ISSUE_NUMBER}` | `[{MODEL}] {TITLE_CLEAN} \#{ISSUE_NUMBER}` |
```

`skills/vibe-eval/templates/issue-body.md` 플레이스홀더 치환:
- `{SLUG}` → SLUG
- `{FEATURE_DEF_URL}` → FEATURE_DEF_URL
- `{TARGET_DIR}` → TARGET_DIR (없으면 `(미지정)`)
- `{MODEL_RULES_TABLE}` → 생성된 테이블
- `{ISSUE_NUMBER}` → `{ISSUE_NUMBER}` (임시)

### 5단계: 이슈 생성

```bash
gh issue create \
  --title "{TITLE}" \
  --body "{ISSUE_BODY}" \
  --label "eval" \
  --label "project:{PROJECT}"
```

생성된 ISSUE_NUMBER 저장 후 본문의 `{ISSUE_NUMBER}` 플레이스홀더를 실제 번호로 치환:

```bash
gh issue edit {ISSUE_NUMBER} --body "{ISSUE_BODY_FINAL}"
```

### 6단계: 세션 파일 생성

`COMBINED_SLUG = {SLUG}-{ISSUE_NUMBER}`

각 모델에 대해 `vibe-sessions/{COMBINED_SLUG}/{MODEL_FILENAME}.md` 생성.
`skills/vibe-eval/templates/session.md` 플레이스홀더 치환.

### 7단계: 커밋 및 결과 출력

```bash
git add feature-definitions/{SLUG}.md vibe-sessions/{COMBINED_SLUG}/
git commit -m "chore: init vibe-eval session for {TITLE} [L{LEVEL}] #{ISSUE_NUMBER}"
```

결과:
```
✅ Vibe Eval 이슈 생성 완료

Issue: #{ISSUE_NUMBER} — {TITLE}
URL: https://github.com/{REPO}/issues/{ISSUE_NUMBER}
슬러그: {COMBINED_SLUG}

모델별 브랜치:
{각 모델별 브랜치명 목록}
```
