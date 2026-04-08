# /vibe-eval create

바이브코딩 평가 세션을 위한 GitHub Issue를 생성한다.

## 트리거

사용자가 `/vibe-eval create {feature-file}` 를 실행할 때.

## 사전 확인

1. `gh auth status` — GitHub CLI 인증 상태 확인
2. `{feature-file}` 파일이 존재하는지 확인
3. 현재 작업 디렉토리가 레포 루트인지 확인 (`git rev-parse --show-toplevel`)

## 실행 절차

### 1단계: feature 파일 파싱

feature.md 파일을 읽어 다음 값을 추출한다:

- **TITLE**: 파일의 `#` 헤딩 (예: `mall/codebase-analysis [L2]`)
- **TITLE_CLEAN**: TITLE에서 ` [L{n}]` 제거 (예: `mall/codebase-analysis`)
- **SLUG**: TITLE_CLEAN에서 `/`를 `-`로 치환 (예: `mall-codebase-analysis`)
- **LEVEL**: `[L{n}]`에서 n 추출 (예: `2`)
- **PROJECT**: SLUG에서 첫 번째 `-` 앞 부분 (예: `mall`)
- **COMMON_PROMPT**: `## 공통 프롬프트` 섹션 내용
- **MODELS**: `## 참여 모델` 섹션의 목록 (각 줄에서 `- ` 제거)
- **REPO**: `gh repo view --json nameWithOwner -q .nameWithOwner`
- **OWNER/REPO**: REPO를 `/`로 분리

### 2단계: Label 생성

다음 라벨을 생성한다. 이미 존재하면 스킵:

```bash
gh label create "eval" --color "0075ca" --description "Vibe eval 이슈" 2>/dev/null || true
gh label create "project:{PROJECT}" --color "e4e669" --description "{PROJECT} 프로젝트" 2>/dev/null || true
```

### 3단계: Issue 생성

FEATURE_DEF_URL = `https://github.com/{OWNER}/{REPO}/blob/main/feature-definitions/{SLUG}.md`

**모델별 슬러그 변환 규칙 (MODEL_SLUG):**
- 소문자로 변환
- 공백 → `-`
- `+지침서` → `-guide`
- 특수문자 제거

예시:
| 모델명 | MODEL_SLUG |
|--------|-----------|
| Claude | claude |
| Wyhill | wyhill |
| Wyhill+지침서 | wyhill-guide |
| Cortex Code | cortex-code |
| Antigravity | antigravity |

MODEL_FILENAME = MODEL_SLUG (세션 파일명으로 사용)

**MODEL_RULES_TABLE 동적 생성:**

각 모델에 대해 아래 형식으로 테이블 행을 생성한다.  
ISSUE_NUMBER는 이 단계에서 아직 모르므로 `{ISSUE_NUMBER}` 플레이스홀더로 표시하고,  
Issue 생성 후 실제 번호로 치환한다.

```
| {MODEL} | `vibe/{MODEL_SLUG}/{SLUG}-{ISSUE_NUMBER}` | `[{MODEL}] {TITLE_CLEAN} #{ISSUE_NUMBER}` |
```

**MODEL_CHECKLIST 동적 생성:**

```
- [ ] {MODEL} — `vibe/{MODEL_SLUG}/{SLUG}-{ISSUE_NUMBER}`
```

`skills/vibe-eval/templates/issue-body.md`의 플레이스홀더를 치환하여 Issue 본문 작성:
- `{TITLE}` → TITLE
- `{TITLE_CLEAN}` → TITLE_CLEAN
- `{SLUG}` → SLUG
- `{FEATURE_DEF_URL}` → 기능 정의서 GitHub blob URL
- `{MODEL_RULES_TABLE}` → 모델별 브랜치/PR 규칙 테이블
- `{MODEL_CHECKLIST}` → 모델별 완료 체크박스

```bash
gh issue create \
  --title "{TITLE}" \
  --body "{ISSUE_BODY}" \
  --label "eval" \
  --label "project:{PROJECT}"
```

생성된 Issue 번호를 ISSUE_NUMBER로 저장.

Issue 본문의 `{ISSUE_NUMBER}` 플레이스홀더를 실제 번호로 치환한 뒤 Issue를 수정:
```bash
gh issue edit {ISSUE_NUMBER} --body "{ISSUE_BODY_FINAL}"
```

**COMBINED_SLUG** = `{SLUG}-{ISSUE_NUMBER}` (예: `mall-codebase-analysis-1`)

### 4단계: 세션 MD 템플릿 생성

각 모델에 대해:

경로: `vibe-sessions/{COMBINED_SLUG}/{MODEL_FILENAME}.md`

- 디렉토리 생성: `mkdir -p vibe-sessions/{COMBINED_SLUG}`
- 파일이 이미 존재하면 스킵 (`--force` 플래그로 덮어쓰기 허용)
- `skills/vibe-eval/templates/session.md`의 플레이스홀더를 치환하여 저장:
  - `{TITLE}` → TITLE
  - `{MODEL}` → 모델명
  - `{DATE}` → 오늘 날짜 (YYYY-MM-DD)
  - `{ISSUE_NUMBER}` → ISSUE_NUMBER
  - `{COMBINED_SLUG}` → COMBINED_SLUG
  - `{BRANCH_NAME}` → `vibe/{MODEL_SLUG}/{COMBINED_SLUG}`
  - 나머지 플레이스홀더는 그대로 유지 (작업자가 직접 채움)

### 5단계: feature-definitions 저장

```bash
mkdir -p feature-definitions
cp {feature-file} feature-definitions/{SLUG}.md
```

- 이미 존재하면 스킵 (`--force` 없이는 덮어쓰지 않음)

### 6단계: 커밋

```bash
git add vibe-sessions/{COMBINED_SLUG}/ feature-definitions/{SLUG}.md
git commit -m "chore: init vibe-eval session for {TITLE}"
```

### 7단계: 결과 요약 출력

```
✅ Vibe Eval 세션 생성 완료

Issue: #{ISSUE_NUMBER} — {TITLE}
URL: https://github.com/{OWNER}/{REPO}/issues/{ISSUE_NUMBER}
슬러그: {COMBINED_SLUG}
프롬프트 레벨: L{LEVEL}

모델별 작업 규칙:
  [Claude]         브랜치: vibe/claude/{COMBINED_SLUG}
  [Wyhill]         브랜치: vibe/wyhill/{COMBINED_SLUG}
  [Wyhill+지침서]  브랜치: vibe/wyhill-guide/{COMBINED_SLUG}
  [Cortex Code]    브랜치: vibe/cortex-code/{COMBINED_SLUG}
  [Antigravity]    브랜치: vibe/antigravity/{COMBINED_SLUG}

세션 파일:
  vibe-sessions/{COMBINED_SLUG}/claude.md
  vibe-sessions/{COMBINED_SLUG}/wyhill.md
  vibe-sessions/{COMBINED_SLUG}/wyhill-guide.md
  vibe-sessions/{COMBINED_SLUG}/cortex-code.md
  vibe-sessions/{COMBINED_SLUG}/antigravity.md

다음 단계:
1. 각 모델에게 이슈 URL을 전달합니다
2. 각 모델이 이슈를 확인 후 본인 브랜치를 생성합니다
3. 바이브코딩 완료 후 세션 MD를 작성합니다
4. PR 생성 시 제목: [모델명] {TITLE_CLEAN} #{ISSUE_NUMBER}
5. PR 본문에 References #{ISSUE_NUMBER} 포함 (Closes 아님)
6. 모든 PR 완료 후: /vibe-eval report {COMBINED_SLUG}
```

## 멱등성 보장

- Issue 중복: `gh issue list --label "eval" --search "{TITLE}"` 로 기존 이슈 확인. 존재하면 재사용 + 경고
- 세션 파일 중복: 스킵 (`--force` 플래그로 덮어쓰기 허용)
- feature-definitions 중복: 스킵 (`--force` 플래그로 덮어쓰기 허용)
