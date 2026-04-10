---
name: wyh:w-create-issue
description: Use when creating a new vibe-eval GitHub issue for this project. Collects feature prompt information step by step and creates the issue automatically.
---

# wyh:w-create-issue

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

`FEATURE_DEF_PATH = .claude/feature-definitions/{SLUG}.md`
`FEATURE_DEF_URL = https://github.com/{REPO}/blob/main/.claude/feature-definitions/{SLUG}.md`

`.claude/skills/vibe-eval/templates/issue-body.md` 플레이스홀더 치환 후 `FEATURE_DEF_PATH`에 저장:
- `{TITLE}` → TITLE
- `{COMMON_PROMPT}` → COMMON_PROMPT
- `{TARGET_DIR}` → TARGET_DIR (없으면 `(미지정)`)
- `{ISSUE_NUMBER}` → `{ISSUE_NUMBER}` (임시)

이미 존재하면 덮어쓰기 전 확인.

### 3단계: 라벨 생성 (없으면)

```bash
gh label create "eval" --color "0075ca" --description "Vibe eval 이슈" 2>/dev/null || true
gh label create "project:{PROJECT}" --color "e4e669" --description "{PROJECT} 프로젝트" 2>/dev/null || true
```

### 4단계: 이슈 생성

```bash
gh issue create \
  --title "{TITLE}" \
  --body-file "{FEATURE_DEF_PATH}" \
  --label "eval" \
  --label "project:{PROJECT}"
```

생성된 ISSUE_NUMBER 저장 후 feature.md의 `{ISSUE_NUMBER}` 플레이스홀더를 실제 번호로 치환하고 파일 업데이트.

### 5단계: 모델 브랜치에 issue-{N}.md 배포

각 참여 모델에 대해 아래를 실행한다.

1. `.claude/skills/vibe-eval/templates/issue-task.md` 파일을 읽어 아래 변수를 치환한다:
   - `{MODEL}` → 모델 브랜치 키 (vibe-models.json의 키, 예: claude, antigravity)
   - `{ISSUE_NUMBER}` → 실제 이슈 번호
   - `{SLUG}` → SLUG
   - `{COMMON_PROMPT}` → COMMON_PROMPT
   - `{TARGET_DIR}` → TARGET_DIR
   - `{TITLE}` → TITLE

2. 치환된 내용을 base64로 인코딩한다.

3. 기존 파일 SHA 조회 (파일이 이미 있으면 덮어쓰기 위해 필요):
   ```bash
   FILE_PATH="{TARGET_DIR}/task/issue-{ISSUE_NUMBER}.md"
   SHA=$(gh api "repos/{REPO}/contents/$FILE_PATH?ref={MODEL_BRANCH}" --jq '.sha' 2>/dev/null || echo "")
   ```

4. GitHub API로 파일 push:
   ```bash
   # SHA가 있으면 payload에 포함 (덮어쓰기), 없으면 신규 생성
   gh api -X PUT "repos/{REPO}/contents/$FILE_PATH" \
     --field message="chore: add issue-{ISSUE_NUMBER} task file [skip ci]" \
     --field content="$(echo -n '{치환된내용}' | base64)" \
     --field branch="{MODEL_BRANCH}" \
     [--field sha="$SHA" (SHA가 있는 경우만)]
   ```

5. 성공 시 `✅ {MODEL_NAME} 브랜치 배포 완료`, 실패 시 `⚠️ {MODEL_NAME} 브랜치 배포 실패 (계속 진행)` 출력 후 다음 모델로 넘어간다.

TARGET_DIR이 미지정인 경우 FILE_PATH는 `task/issue-{ISSUE_NUMBER}.md`로 사용한다.

### 6단계: 세션 파일 생성

`COMBINED_SLUG = {SLUG}-{ISSUE_NUMBER}`

각 모델에 대해 `.claude/vibe-sessions/{COMBINED_SLUG}/{MODEL_FILENAME}.md` 생성.
`.claude/skills/vibe-eval/templates/session.md` 플레이스홀더 치환.

### 7단계: 커밋 및 결과 출력

```bash
git add .claude/feature-definitions/{SLUG}.md .claude/vibe-sessions/{COMBINED_SLUG}/
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
