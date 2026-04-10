# Vibe Eval Guide Delivery Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 이슈 바디를 사람이 읽는 용도로 정리하고, AI 개발자용 `issue-{N}.md`와 리뷰어용 `review-{N}.md`를 각 모델 브랜치에 자동으로 배포하는 시스템을 구축한다.

**Architecture:** 이슈 생성 시 `/wyh:w-create-issue`가 각 모델 브랜치에 `{TARGET_DIR}/task/issue-{N}.md`를 push하고, PR 생성 시 GitHub Actions(`vibe-review-dispatch.yml`)가 나머지 모델 브랜치에 `{TARGET_DIR}/task/review-{N}.md`를 자동 push한다.

**Tech Stack:** Git (worktree), GitHub Actions (github-script@v7, GITHUB_TOKEN), Markdown templates

**Spec:** `docs/superpowers/specs/2026-04-10-vibe-eval-guide-delivery-design.md`

---

## File Map

| 작업 | 파일 |
|------|------|
| Modify | `.claude/skills/vibe-eval/templates/issue-body.md` |
| Create | `.claude/skills/vibe-eval/templates/issue-task.md` |
| Create | `.claude/skills/vibe-eval/templates/review-task.md` |
| Modify | `.claude/commands/wyh/w-create-issue.md` |
| Create | `.github/workflows/vibe-review-dispatch.yml` |

---

## Task 1: `issue-body.md` 템플릿 업데이트

이슈 바디를 사람이 읽는 용도로 정리한다.  
개발 가이드(MODEL_GIT_COMMANDS, 제출 규칙)와 리뷰어 가이드를 제거하고, 공통 프롬프트·평가지표·issue 파일 경로 안내만 남긴다.

**Files:**
- Modify: `.claude/skills/vibe-eval/templates/issue-body.md`

- [ ] **Step 1: issue-body.md 교체**

`.claude/skills/vibe-eval/templates/issue-body.md` 를 아래 내용으로 교체한다:

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

- [ ] **Step 2: 커밋**

```bash
git add .claude/skills/vibe-eval/templates/issue-body.md
git commit -m "refactor: simplify issue body to human-readable format"
```

---

## Task 2: `issue-task.md` 템플릿 생성

AI 개발자용 지시 파일 템플릿을 만든다.  
모델별 치환 변수: `{MODEL}`, `{ISSUE_NUMBER}`, `{SLUG}`, `{COMMON_PROMPT}`, `{TARGET_DIR}`, `{TITLE}`

**Files:**
- Create: `.claude/skills/vibe-eval/templates/issue-task.md`

- [ ] **Step 1: issue-task.md 생성**

`.claude/skills/vibe-eval/templates/issue-task.md` 파일을 아래 내용으로 생성한다:

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

- [ ] **Step 2: 커밋**

```bash
git add .claude/skills/vibe-eval/templates/issue-task.md
git commit -m "feat: add issue-task template for AI developer"
```

---

## Task 3: `review-task.md` 템플릿 생성

AI 리뷰어용 지시 파일 템플릿을 만든다.  
치환 변수: `{TITLE}`, `{ISSUE_NUMBER}`, `{PR_NUMBER}`, `{PR_AUTHOR_MODEL}`, `{OWNER}`, `{REPO}`

**Files:**
- Create: `.claude/skills/vibe-eval/templates/review-task.md`

- [ ] **Step 1: review-task.md 생성**

`.claude/skills/vibe-eval/templates/review-task.md` 파일을 아래 내용으로 생성한다:

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

- [ ] **Step 2: 커밋**

```bash
git add .claude/skills/vibe-eval/templates/review-task.md
git commit -m "feat: add review-task template for AI reviewer"
```

---

## Task 4: `w-create-issue.md` 업데이트

이슈 생성 완료 후 각 참여 모델 브랜치에 `{TARGET_DIR}/task/issue-{N}.md`를 push하는 단계를 추가한다.

**Files:**
- Modify: `.claude/commands/wyh/w-create-issue.md`

### 변경 개요

- **2단계 (`feature.md` 작성)**: `MODEL_GIT_COMMANDS` 생성 로직 제거. `issue-body.md` 템플릿에서 해당 플레이스홀더 사라졌으므로 불필요.
- **4단계 (이슈 생성)**: 이슈 바디를 feature.md 링크 대신 `issue-body.md` 템플릿 치환 내용으로 직접 생성.
- **5단계 (신규)**: 이슈번호 확정 후, 참여 모델 각 브랜치에 `issue-{N}.md` push.
- **기존 5단계** → **6단계**로 번호 변경 (세션 파일 생성).
- **기존 6단계** → **7단계**로 번호 변경 (커밋 및 결과 출력). 커밋에 세션 파일만 포함 (issue 파일은 이미 각 브랜치에 직접 push).

- [ ] **Step 1: 2단계 수정 — MODEL_GIT_COMMANDS 로직 제거**

`w-create-issue.md`의 2단계에서 `MODEL_GIT_COMMANDS` 생성 및 치환 로직을 제거한다.

`FEATURE_DEF_PATH`와 `FEATURE_DEF_URL` 변수는 feature.md 저장 경로로 유지한다.

2단계는 `issue-body.md` 템플릿을 치환하여 `FEATURE_DEF_PATH`에 저장하되, 치환 변수를 아래로 줄인다:
- `{TITLE}` → TITLE
- `{COMMON_PROMPT}` → COMMON_PROMPT
- `{TARGET_DIR}` → TARGET_DIR (없으면 `(미지정)`)
- `{ISSUE_NUMBER}` → `{ISSUE_NUMBER}` (임시, 이슈 생성 후 실제 번호로 치환)

- [ ] **Step 2: 4단계 수정 — 이슈 바디를 feature.md 내용으로 직접 생성**

이슈 생성 시 body를 feature.md 링크 대신 feature.md 파일 내용으로 전달한다:

```bash
gh issue create \
  --title "{TITLE}" \
  --body-file "{FEATURE_DEF_PATH}" \
  --label "eval" \
  --label "project:{PROJECT}"
```

`--body-file` 플래그는 파일 내용을 이슈 바디로 사용한다.  
이슈 생성 후 반환된 ISSUE_NUMBER로 feature.md의 `{ISSUE_NUMBER}` 플레이스홀더를 실제 번호로 치환한다.

- [ ] **Step 3: 5단계 추가 — 모델 브랜치에 issue-{N}.md push**

이슈번호 확정 직후 아래 로직을 실행하는 5단계를 추가한다.  
**push 방식: `gh api`** (git worktree 불사용 — Windows/Mac/Linux 모두 안전, checkout 불필요)

각 참여 모델에 대해:
1. `issue-task.md` 템플릿을 읽어 아래 변수를 치환한 내용을 준비:
   - `{MODEL}` → 모델 브랜치 키 (예: `claude`)
   - `{ISSUE_NUMBER}` → 실제 이슈 번호
   - `{SLUG}` → SLUG
   - `{COMMON_PROMPT}` → COMMON_PROMPT
   - `{TARGET_DIR}` → TARGET_DIR
   - `{TITLE}` → TITLE
2. 기존 파일 SHA 조회 (덮어쓰기용, 없으면 신규 생성):
   ```bash
   FILE_PATH="{TARGET_DIR}/task/issue-{ISSUE_NUMBER}.md"
   SHA=$(gh api "repos/{REPO}/contents/$FILE_PATH?ref={MODEL}" --jq '.sha' 2>/dev/null || echo "")
   ```
3. 파일 내용을 base64로 인코딩 후 GitHub API로 push:
   ```bash
   CONTENT=$(echo -n "{치환된 파일 내용}" | base64)
   PAYLOAD="{\"message\":\"chore: add issue-{ISSUE_NUMBER} task file [skip ci]\",\"content\":\"$CONTENT\",\"branch\":\"{MODEL}\"}"
   [ -n "$SHA" ] && PAYLOAD=$(echo "$PAYLOAD" | jq --arg sha "$SHA" '. + {sha: $sha}')
   gh api -X PUT "repos/{REPO}/contents/$FILE_PATH" --input - <<< "$PAYLOAD"
   ```

push 실패 시 오류 메시지를 출력하고 다음 모델로 넘어간다.

- [ ] **Step 4: 기존 5·6단계를 6·7단계로 번호 조정**

기존 5단계(세션 파일 생성)를 6단계로, 기존 6단계(커밋 및 결과 출력)를 7단계로 번호를 바꾼다.  
7단계 커밋 대상에서 feature.md 경로만 포함하고, issue 파일은 이미 각 모델 브랜치에 push되었으므로 제외한다.

- [ ] **Step 5: 커밋**

```bash
git add .claude/commands/wyh/w-create-issue.md
git commit -m "feat: push issue task files to model branches on issue creation"
```

---

## Task 5: `vibe-review-dispatch.yml` 생성

PR `opened` 이벤트 시 참여 모델 브랜치에 `review-{N}.md`를 자동 push하는 워크플로우를 만든다.

**Files:**
- Create: `.github/workflows/vibe-review-dispatch.yml`

**이슈번호 파싱 전략:** 브랜치명 `vibe/{MODEL}/{SLUG}-{ISSUE_NUMBER}` 에서 맨 끝의 `-숫자` 패턴을 추출한다.  
정규식: `/^vibe\/[^\/]+\/.+-(\d+)$/` → 마지막 캡처 그룹이 이슈번호.

**참여 모델 파악:** `{TARGET_DIR}/task/issue-{ISSUE_NUMBER}.md` 파일이 해당 모델 브랜치에 존재하는지 GitHub API로 확인한다.

- [ ] **Step 1: vibe-review-dispatch.yml 생성**

`.github/workflows/vibe-review-dispatch.yml` 파일을 아래 내용으로 생성한다:

```yaml
name: Vibe Review Dispatch

on:
  pull_request:
    types: [opened]

permissions:
  contents: write
  pull-requests: read

env:
  FORCE_JAVASCRIPT_ACTIONS_TO_NODE24: true

jobs:
  dispatch-review:
    name: "review-{N}.md 배포"
    runs-on: ubuntu-latest
    steps:
      - name: Dispatch review task files
        uses: actions/github-script@v7
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            const headBranch = context.payload.pull_request.head.ref;
            const prNumber = context.payload.pull_request.number;
            const prTitle = context.payload.pull_request.title;
            const owner = context.repo.owner;
            const repo = context.repo.repo;

            // vibe/* 브랜치만 처리
            const branchMatch = headBranch.match(/^vibe\/([^\/]+)\/.+-(\d+)$/);
            if (!branchMatch) {
              console.log(`[건너뜀] vibe/* 패턴 불일치: ${headBranch}`);
              return;
            }

            const authorModel = branchMatch[1];
            const issueNumber = branchMatch[2];

            console.log(`[시작] PR #${prNumber}, 모델: ${authorModel}, 이슈: #${issueNumber}`);

            // vibe-models.json 읽기
            const configRes = await github.rest.repos.getContent({
              owner, repo,
              path: '.github/vibe-models.json',
              ref: context.payload.pull_request.base.repo.default_branch
            });
            const config = JSON.parse(Buffer.from(configRes.data.content, 'base64').toString());
            const allModels = Object.keys(config.models);

            // issue-task.md 템플릿 읽기
            const templateRes = await github.rest.repos.getContent({
              owner, repo,
              path: '.claude/skills/vibe-eval/templates/review-task.md',
              ref: context.payload.pull_request.base.repo.default_branch
            });
            const template = Buffer.from(templateRes.data.content, 'base64').toString();

            // issueTitle: PR 제목에서 [모델명] 접두사 제거
            const issueTitle = prTitle.replace(/^\[[^\]]+\]\s*/, '').replace(/\s*#\d+$/, '').trim();

            // vibe-projects.json 읽기 (루프 밖 — 1회만)
            let projects = {};
            try {
              const projectsRes = await github.rest.repos.getContent({
                owner, repo,
                path: '.github/vibe-projects.json',
                ref: context.payload.pull_request.base.repo.default_branch
              });
              projects = JSON.parse(Buffer.from(projectsRes.data.content, 'base64').toString()).projects;
            } catch(e) {
              console.log(`[경고] vibe-projects.json 읽기 실패: ${e.message}`);
            }

            // 참여 모델 목록: issue-{N}.md가 존재하는 모델 브랜치 파악
            const participatingModels = [];
            for (const modelKey of allModels) {
              if (modelKey === authorModel) continue;
              const model = config.models[modelKey];

              let foundTaskFile = false;
              let foundTargetDir = '';

              for (const [, proj] of Object.entries(projects)) {
                try {
                  await github.rest.repos.getContent({
                    owner, repo,
                    path: `${proj.folder}/task/issue-${issueNumber}.md`,
                    ref: model.base_branch
                  });
                  foundTaskFile = true;
                  foundTargetDir = proj.folder;
                  break;
                } catch(e) { /* 없으면 다음 프로젝트 */ }
              }

              if (foundTaskFile) {
                participatingModels.push({ modelKey, model, targetDir: foundTargetDir });
              } else {
                console.log(`[건너뜀] ${modelKey}: issue-${issueNumber}.md 없음 (미참여 모델)`);
              }
            }

            if (participatingModels.length === 0) {
              console.log('[완료] 리뷰 파일을 배포할 참여 모델 없음');
              return;
            }

            // 각 참여 모델 브랜치에 review-{N}.md push
            for (const { modelKey, model, targetDir } of participatingModels) {
              const filePath = `${targetDir}/task/review-${issueNumber}.md`;
              const content = template
                .replace(/{TITLE}/g, issueTitle)
                .replace(/{ISSUE_NUMBER}/g, issueNumber)
                .replace(/{PR_NUMBER}/g, String(prNumber))
                .replace(/{PR_AUTHOR_MODEL}/g, config.models[authorModel]?.display_name || authorModel)
                .replace(/{OWNER}/g, owner)
                .replace(/{REPO}/g, repo);

              const encodedContent = Buffer.from(content).toString('base64');

              // 기존 파일 SHA 조회 (덮어쓰기용)
              let sha = undefined;
              try {
                const existing = await github.rest.repos.getContent({
                  owner, repo,
                  path: filePath,
                  ref: model.base_branch
                });
                sha = existing.data.sha;
              } catch(e) { /* 신규 파일 */ }

              try {
                await github.rest.repos.createOrUpdateFileContents({
                  owner, repo,
                  path: filePath,
                  message: `chore: add review-${issueNumber} task file [skip ci]`,
                  content: encodedContent,
                  branch: model.base_branch,
                  ...(sha ? { sha } : {})
                });
                console.log(`[완료] ${modelKey} 브랜치 → ${filePath}`);
              } catch(e) {
                console.log(`[실패] ${modelKey} 브랜치 push 실패: ${e.message}`);
              }
            }
```

> **주의:** GitHub API를 통한 파일 생성(`createOrUpdateFileContents`)은 `contents: write` 권한으로 가능하며, branch protection이 있어도 `GITHUB_TOKEN`은 허용된다.

- [ ] **Step 2: YAML 문법 검증**

```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/vibe-review-dispatch.yml'))" && echo "YAML OK"
```

또는 `npx js-yaml .github/workflows/vibe-review-dispatch.yml`

- [ ] **Step 3: 커밋**

```bash
git add .github/workflows/vibe-review-dispatch.yml
git commit -m "feat: add vibe-review-dispatch workflow for auto review file push"
```

---

## Task 6: 통합 검증

- [ ] **Step 1: `/wyh:w-create-issue` 실행 테스트**

테스트 이슈를 생성해 다음을 확인한다:
- 이슈 바디에 공통 프롬프트·평가지표·`issue-{N}.md` 경로가 올바르게 표시되는가
- 각 모델 브랜치에 `{TARGET_DIR}/task/issue-{N}.md`가 생성되었는가
- 파일 내용에 브랜치명(MODEL)이 모델별로 올바르게 치환되었는가

```bash
# 각 모델 브랜치에서 파일 존재 확인
git show claude:{TARGET_DIR}/task/issue-{N}.md
git show antigravity:{TARGET_DIR}/task/issue-{N}.md
```

- [ ] **Step 2: GitHub Actions 실행 확인**

테스트 PR을 생성한 뒤 Actions 탭에서 `Vibe Review Dispatch` 워크플로우가 실행되었는지, 각 참여 모델 브랜치에 `review-{N}.md`가 생성되었는지 확인한다.

```bash
git show {REVIEWER_MODEL}:{TARGET_DIR}/task/review-{N}.md
```

- [ ] **Step 3: 최종 커밋 확인**

```bash
git log --oneline -10
```
