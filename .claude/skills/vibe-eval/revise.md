# /vibe-eval revise

기존 이슈의 수정 버전을 새 이슈로 생성하고, 기존 이슈에 대체 안내 코멘트를 추가한다.

## 트리거

사용자가 `/vibe-eval revise {combined-slug}` 를 실행할 때.

`combined-slug` 형식: `{slug}-{issue-number}` (예: `mall-codebase-analysis-1`)

## 사전 확인

1. REPO = `gh repo view --json nameWithOwner -q .nameWithOwner`
2. OWNER/REPO: REPO를 `/`로 분리
3. COMBINED_SLUG: 사용자 입력 인수에서 직접 사용
4. OLD_ISSUE_NUMBER: COMBINED_SLUG의 마지막 `-` 뒤 숫자 (예: `1`)
5. SLUG: COMBINED_SLUG에서 `-{OLD_ISSUE_NUMBER}` 제거 (예: `mall-codebase-analysis`)
6. 기존 이슈 조회:
```bash
gh issue view {OLD_ISSUE_NUMBER} --json title,body,labels
```
- 이슈가 없으면: "❌ Issue #{OLD_ISSUE_NUMBER}를 찾을 수 없습니다." 출력 후 종료

기존 이슈 제목에서:
- **OLD_TITLE**: 이슈 제목 전체 (예: `mall/codebase-analysis [L2]`)
- **TITLE_CLEAN**: OLD_TITLE에서 ` [L{n}]` 제거 (예: `mall/codebase-analysis`)
- **LEVEL**: `[L{n}]`에서 추출
- **PROJECT**: SLUG에서 첫 번째 `-` 앞 부분

## 실행 절차

### 1단계: 수정 이유 입력받기

사용자에게 묻는다:

"수정 이유를 입력해주세요 (기존 이슈 #{OLD_ISSUE_NUMBER}에 코멘트로 추가됩니다):"

입력받은 내용을 REVISION_REASON으로 저장.

### 2단계: 새 Issue 생성

`feature-definitions/{SLUG}.md` 파일을 읽어 COMMON_PROMPT, MODELS를 파싱한다.

FEATURE_DEF_URL = `https://github.com/{OWNER}/{REPO}/blob/main/feature-definitions/{SLUG}.md`

`skills/vibe-eval/templates/issue-body.md`의 플레이스홀더를 치환하여 새 Issue 본문 작성.  
(ISSUE_NUMBER는 새로 생성될 번호이므로 먼저 플레이스홀더로 작성 후 생성 뒤 수정)

```bash
gh issue create \
  --title "{OLD_TITLE}" \
  --body "{ISSUE_BODY}" \
  --label "eval" \
  --label "project:{PROJECT}"
```

NEW_ISSUE_NUMBER 저장.

Issue 본문의 `{ISSUE_NUMBER}` 플레이스홀더를 실제 번호로 치환 후 수정:
```bash
gh issue edit {NEW_ISSUE_NUMBER} --body "{ISSUE_BODY_FINAL}"
```

**NEW_COMBINED_SLUG** = `{SLUG}-{NEW_ISSUE_NUMBER}` (예: `mall-codebase-analysis-3`)
NEW_ISSUE_URL = `https://github.com/{OWNER}/{REPO}/issues/{NEW_ISSUE_NUMBER}`

### 3단계: 세션 MD 템플릿 생성

각 모델에 대해 `vibe-sessions/{NEW_COMBINED_SLUG}/{MODEL_FILENAME}.md` 생성.  
(create 스킬의 4단계와 동일한 방식)

### 4단계: 기존 Issue에 코멘트 추가

```bash
gh issue comment {OLD_ISSUE_NUMBER} --body "⚠️ 이 이슈는 [#{NEW_ISSUE_NUMBER}]({NEW_ISSUE_URL})으로 대체되었습니다.

**수정 이유:** {REVISION_REASON}

새 이슈에서 작업을 진행해주세요."
```

> 기존 이슈는 close하지 않음 — 참고용으로 유지

### 5단계: 커밋

```bash
git add vibe-sessions/{NEW_COMBINED_SLUG}/
git commit -m "chore: init vibe-eval revision for {OLD_TITLE} (#{NEW_ISSUE_NUMBER})"
```

### 6단계: 결과 요약 출력

```
✅ 수정 버전 생성 완료

기존 Issue: #{OLD_ISSUE_NUMBER} — {OLD_TITLE}
신규 Issue: #{NEW_ISSUE_NUMBER} — {NEW_ISSUE_URL}
수정 이유: {REVISION_REASON}

기존 이슈 #{OLD_ISSUE_NUMBER}에 대체 안내 코멘트 추가됨.

새 슬러그: {NEW_COMBINED_SLUG}
세션 파일:
  vibe-sessions/{NEW_COMBINED_SLUG}/claude.md
  vibe-sessions/{NEW_COMBINED_SLUG}/wyhill.md
  ...

다음 단계:
1. 각 모델에게 새 이슈 URL을 전달합니다
   {NEW_ISSUE_URL}
2. /vibe-eval report {NEW_COMBINED_SLUG}
```
