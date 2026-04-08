# /vibe-eval create

바이브코딩 평가 세션을 위한 GitHub Milestone + 모델별 Issue를 자동 생성한다.

## 트리거

사용자가 `/vibe-eval create {feature-file}` 또는 `vibe-eval create {feature-file}` 을 실행할 때.

## 사전 확인

실행 전 다음을 확인한다:
1. `gh auth status` — GitHub CLI 인증 상태 확인
2. `{feature-file}` 파일이 존재하는지 확인
3. 현재 작업 디렉토리가 레포 루트인지 확인 (`git rev-parse --show-toplevel`)

## 실행 절차

### 1단계: feature 파일 파싱

feature.md 파일을 읽어 다음 값을 추출한다:

- **MILESTONE_TITLE**: 파일의 `#` 헤딩 (예: `mall/login-feature [L2]`)
- **SLUG**: MILESTONE_TITLE에서 ` [L{n}]` 제거 후 `/`를 `-`로 치환 (예: `mall-login-feature`)
- **LEVEL**: `[L{n}]`에서 n 추출 (예: `2`)
- **PROJECT**: SLUG에서 첫 번째 `-` 앞 부분 (예: `mall`)
- **COMMON_PROMPT**: `## 공통 프롬프트` 섹션 내용
- **MODELS**: `## 참여 모델` 섹션의 목록 (각 줄에서 `- ` 제거)
- **REPO**: `gh repo view --json nameWithOwner -q .nameWithOwner` 로 조회
- **OWNER/REPO**: REPO를 `/`로 분리

### 2단계: Milestone 생성

기존 마일스톤 확인:
```bash
gh api repos/{OWNER}/{REPO}/milestones --jq '.[] | select(.title == "{MILESTONE_TITLE}") | .number'
```

- 존재하면: 기존 번호 사용, "⚠️ 마일스톤 '{MILESTONE_TITLE}' 이미 존재합니다. 기존 것을 재사용합니다." 출력
- 없으면: 새로 생성
```bash
gh api repos/{OWNER}/{REPO}/milestones \
  -X POST \
  -f title="{MILESTONE_TITLE}" \
  -f description="{COMMON_PROMPT}"
```
생성된 마일스톤 번호(`.number`)를 MILESTONE_NUMBER 변수에 저장.

MILESTONE_URL = `https://github.com/{OWNER}/{REPO}/milestone/{MILESTONE_NUMBER}`

### 3단계: Label 생성

다음 라벨을 생성한다. 이미 존재하면 스킵:

```bash
# eval 라벨
gh label create "eval" --color "0075ca" --description "Vibe eval 이슈" 2>/dev/null || true

# project 라벨
gh label create "project:{PROJECT}" --color "e4e669" --description "{PROJECT} 프로젝트" 2>/dev/null || true

# 각 모델 라벨 (MODEL_FILENAME = 모델명을 소문자로, 공백/점을 `-`로 치환)
gh label create "model:{MODEL}" --color "d93f0b" --description "{MODEL} 모델" 2>/dev/null || true
```

### 4단계: 모델별 Issue + 세션 파일 생성

각 모델에 대해:

**a) Issue 중복 확인**
```bash
gh issue list \
  --label "model:{MODEL}" \
  --label "eval" \
  --milestone "{MILESTONE_TITLE}" \
  --json number,title \
  --jq 'length'
```
- 1 이상이면: "⚠️ [{MODEL}] Issue 이미 존재. 스킵합니다." 출력하고 다음 모델로

**b) Issue 생성**

MODEL_FILENAME = MODEL을 소문자로, 공백/점을 하이픈으로 치환 (예: `claude-sonnet-4-5`)

FEATURE_DEF_URL = `https://github.com/{OWNER}/{REPO}/blob/main/feature-definitions/{SLUG}.md`

Issue 본문은 `skills/vibe-eval/templates/issue-body.md`의 플레이스홀더를 치환하여 작성:
- `{MILESTONE_TITLE}` → 마일스톤 제목
- `{MILESTONE_URL}` → 마일스톤 URL
- `{MODEL}` → 모델명
- `{SLUG}` → 슬러그
- `{FEATURE_DEF_URL}` → 기능 정의서 URL
- `{COMMON_PROMPT}` → 공통 프롬프트
- `{MODEL_FILENAME}` → 모델 파일명

```bash
gh issue create \
  --title "[{MODEL}] {MILESTONE_TITLE}" \
  --body "{ISSUE_BODY}" \
  --milestone "{MILESTONE_TITLE}" \
  --label "eval" \
  --label "project:{PROJECT}" \
  --label "model:{MODEL}"
```
생성된 Issue 번호를 ISSUE_NUMBER로 저장.

**c) 세션 MD 템플릿 생성**

경로: `vibe-sessions/{SLUG}/{MODEL_FILENAME}.md`

- 디렉토리가 없으면 생성: `mkdir -p vibe-sessions/{SLUG}`
- 파일이 이미 존재하면: "⚠️ 세션 파일 이미 존재. `--force` 플래그 없이는 스킵합니다." 출력하고 스킵
- `skills/vibe-eval/templates/session.md`의 플레이스홀더를 치환하여 저장:
  - `{MILESTONE_TITLE}` → 마일스톤 제목
  - `{MODEL}` → 모델명
  - `{COMMON_PROMPT}` → 공통 프롬프트 (각 줄 앞에 `> ` 추가한 인용문 형식)
  - `{DATE}` → 오늘 날짜 (YYYY-MM-DD)
  - `{ISSUE_NUMBER}` → 생성된 Issue 번호
  - `{BRANCH_NAME}` → `issue/{ISSUE_NUMBER}-{MODEL_FILENAME}-{SLUG}` 제안값
  - 나머지 플레이스홀더는 그대로 유지 (작업자가 직접 채움)

### 5단계: feature-definitions 저장

```bash
mkdir -p feature-definitions
cp {feature-file} feature-definitions/{SLUG}.md
```
- 이미 존재하면 스킵 (`--force` 없이는 덮어쓰지 않음)

### 6단계: 생성된 파일 커밋

```bash
git add vibe-sessions/{SLUG}/ feature-definitions/{SLUG}.md
git commit -m "chore: init vibe-eval session for {MILESTONE_TITLE}"
```

### 7단계: 결과 요약 출력

```
✅ Vibe Eval 세션 생성 완료

마일스톤: {MILESTONE_TITLE} ({MILESTONE_URL})
슬러그: {SLUG}
프롬프트 레벨: L{LEVEL}

생성된 Issue:
  [{MODEL_1}] → #{ISSUE_1}  브랜치 제안: issue/{ISSUE_1}-{MODEL_1_FILENAME}-{SLUG}
  [{MODEL_2}] → #{ISSUE_2}  브랜치 제안: issue/{ISSUE_2}-{MODEL_2_FILENAME}-{SLUG}
  ...

세션 파일:
  vibe-sessions/{SLUG}/{MODEL_1_FILENAME}.md
  vibe-sessions/{SLUG}/{MODEL_2_FILENAME}.md
  ...

다음 단계:
1. 각 참여자가 담당 Issue를 확인합니다
2. 제안된 브랜치명으로 브랜치를 생성합니다
3. 바이브코딩 완료 후 세션 MD 파일을 작성합니다
4. PR을 생성하고 Issue를 연결합니다
5. 모든 PR 완료 후: /vibe-eval report {SLUG}
```

## 멱등성 보장

- 마일스톤 중복: 재사용 + 경고
- Issue 중복: 스킵 + 경고
- 세션 파일 중복: 스킵 (`--force` 플래그로 덮어쓰기 허용)
- feature-definitions 중복: 스킵 (`--force` 플래그로 덮어쓰기 허용)
