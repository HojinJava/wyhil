# wyhil — Vibe Eval

> **W**hich AI model is best for **Y**our **H**uman-**I**n-the-**L**oop coding?

여러 AI 모델이 동일한 프롬프트로 바이브코딩(Vibe Coding)을 진행했을 때,  
각 모델의 결과를 구조화된 데이터로 기록하고 AI가 분석·비교 리포트를 생성하는 평가 시스템입니다.

---

## 흐름도

![vibe-eval 워크플로우](.claude/vibe_eval_workflow.svg)

---

## 1. 이슈 생성

**담당:** 👤 관리자

`/wyh:create-issue` 명령을 대화형으로 실행하여 이슈를 생성합니다.

```
/wyh:create-issue
```

| 입력 항목 | 설명 |
|-----------|------|
| 프로젝트 | 등록된 프로젝트 목록에서 선택 |
| 기능 제목 | 영어로 입력 (예: `server-startup-log`) |
| 레벨 | 1=L1(단순) / 2=L2(중간) / 3=L3(복잡) |
| 공통 프롬프트 | 모든 모델이 수행할 작업 내용 |
| 참여 모델 | 평가에 참여할 모델 선택 |

확인 후 아래가 자동 실행됩니다:

- `.claude/feature-definitions/{slug}.md` 저장
- GitHub Issue 생성
- 각 참여 모델 브랜치에 `task/{project}/issue-{N}.md` 자동 push

→ [작업 지시 템플릿](.claude/skills/vibe-eval/templates/issue-task.md)

---

## 2. 바이브코딩 요청

**담당:** 🙋 사용자

각 AI 모델에게 본인 브랜치를 pull하고 task 파일을 읽으라고 안내합니다.

```
{model} 브랜치 pull 받고 task/{project}/issue-{N}.md 읽고 작업해줘
```

| 규칙 | 내용 |
|------|------|
| 추가 프롬프트 | ❌ 금지 |
| 지침서 적용 | ✅ 허용 (PR 본문에 기록) |

---

## 3. 바이브코딩 진행

**담당:** 🤖 AI 모델

task 파일의 지시에 따라 개발을 진행하고 PR을 생성합니다.

```bash
git fetch origin {model}
git checkout -b vibe/{model}/{slug}-{N} origin/{model}
```

| 작업 항목 | 내용 |
|-----------|------|
| 작업 위치 | `task/{project}/issue-{N}.md` 참고 |
| 브랜치 | `vibe/{model}/{slug}-{issue-number}` |
| PR 대상 | 본인 모델 베이스 브랜치 (`{model}`) |
| PR 제목 | `[{모델명}] {기능명} #{issue-number}` |
| PR → Issue 연결 | `References #{N}` 사용 (`Closes` 아님) |
| 커밋 | squash하여 1개로 제출 |

PR 생성 후 task 파일을 `task_done/`으로 이동합니다 (작업 브랜치 + 모델 베이스 브랜치 모두):

```bash
# 1. 작업 브랜치에서
mkdir -p task_done/{project}
git mv task/{project}/issue-{N}.md task_done/{project}/issue-{N}.md
git commit -m "chore: move issue-{N} task to task_done"
git push origin vibe/{model}/{slug}-{N}

# 2. 모델 베이스 브랜치에서
git checkout {model} && git pull origin {model}
mkdir -p task_done/{project}
git mv task/{project}/issue-{N}.md task_done/{project}/issue-{N}.md
git commit -m "chore: move issue-{N} task to task_done"
git push origin {model}
```

---

## 4. 리뷰 요청

**담당:** 🙋 사용자

PR이 생성되면 다른 모델 브랜치에 `task/{project}/review-{N}.md`가 자동 push됩니다.  
해당 모델에게 브랜치를 pull하고 리뷰 파일을 읽으라고 안내합니다.

```
{model} 브랜치 pull 받고 task/{project}/review-{N}.md 읽고 리뷰해줘
```

---

## 5. 리뷰 진행

**담당:** 🤖 AI 모델

review 파일의 지시에 따라 PR을 검토하고 평가서를 PR 댓글로 등록합니다.

→ [리뷰 지시 템플릿](.claude/skills/vibe-eval/templates/review-task.md)

| 작업 항목 | 내용 |
|-----------|------|
| 작업 위치 | `task/{project}/review-{N}.md` 참고 |
| 평가 기준 | 공통 프롬프트 이행 · 결과물 품질 · 자율 완성도 (각 1~5점) |
| 리뷰 방식 | PR diff만 기준 — 로컬 실행 없음 |
| 제출 방식 | PR 댓글로 평가서 등록 |

리뷰 완료 후 task 파일을 `task_done/`으로 이동합니다:

```bash
mkdir -p task_done/{project}
git mv task/{project}/review-{N}.md task_done/{project}/review-{N}.md
git commit -m "chore: move review-{N} task to task_done"
git push origin {model}
```

---

## 6. 리포트 생성

**담당:** 👤 관리자

모든 모델의 PR 및 리뷰가 완료되면 Issue를 수동으로 종료하고 리포트를 생성합니다.  
Issue에 연결된 PR과 세션 기록을 분석하여 모델별 비교 리포트를 작성합니다.

→ [리포트 생성 가이드](.claude/skills/vibe-eval/report.md)

결과물: `reports/{slug}-{issue-number}/comparison.md`

---

## 참여 모델

| 모델 | 브랜치 슬러그 | 세션 파일명 |
|------|-------------|------------|
| **Claude** | `claude` | `claude.md` |
| **Wyhill** | `wyhill` | `wyhill.md` |
| **Wyhill+지침서** | `wyhill-guide` | `wyhill-guide.md` |
| **Cortex Code** | `cortex-code` | `cortex-code.md` |
| **Antigravity** | `antigravity` | `antigravity.md` |

---

## 디렉터리 구조

```
repo/
├── task/                                 ← 작업·리뷰 지시 파일 (모델 브랜치에 자동 push)
│   └── {project}/
│       ├── issue-{N}.md                  ← AI 개발자용 작업 지시
│       └── review-{N}.md                 ← AI 리뷰어용 리뷰 지시
├── task_done/                            ← 완료 후 이동
│   └── {project}/
│       ├── issue-{N}.md
│       └── review-{N}.md
├── {project}/                            ← 실제 프로젝트 코드
├── .github/
│   ├── vibe-models.json                  ← 참여 모델 정보
│   ├── vibe-projects.json                ← 등록된 프로젝트 목록
│   └── workflows/
│       ├── vibe-pr-validation.yml
│       └── vibe-review-dispatch.yml
├── .gitignore
├── README.md
└── .claude/
    ├── commands/wyh/                     ← 관리자 전용 커맨드
    ├── feature-definitions/              ← 공통 프롬프트 정의서
    │   └── {slug}.md
    ├── vibe-sessions/                    ← 모델별 세션 기록
    │   └── {slug}-{N}/
    │       ├── claude.md
    │       └── ...
    └── skills/vibe-eval/templates/       ← 자동 생성 파일 템플릿
```

### 슬러그 & 네이밍 규칙

```
기능 제목:  mall/codebase-analysis [L2]
slug:       mall-codebase-analysis
Issue #:    1
combined:   mall-codebase-analysis-1

브랜치:  vibe/claude/mall-codebase-analysis-1
PR 제목: [Claude] mall/codebase-analysis #1
세션:    .claude/vibe-sessions/mall-codebase-analysis-1/claude.md
```

---

## 사전 준비

관리자 커맨드(`/wyh:*`)는 내부적으로 GitHub CLI(`gh`)를 사용합니다.

```bash
# macOS
brew install gh

# Windows (winget)
winget install --id GitHub.cli

# Windows (scoop)
scoop install gh

# Linux (apt)
sudo apt install gh
```

설치 후 로그인 (최초 1회):

```bash
gh auth login
```

---

## 관리자 기능

Claude Code에서 `/명령어`로 실행하는 프로젝트 전용 관리 커맨드입니다.

| 커맨드 | 설명 |
|--------|------|
| `/wyh:create-issue` | 이슈 생성 마법사 (프로젝트 → 제목 → 레벨 → 프롬프트 → 모델) |
| `/w-bulk-create-issue <file.json>` | JSON 파일로 이슈 일괄 생성 |
| `/wyh:model` | 등록된 모델 목록 확인 |
| `/wyh:model add <key>` | 새 모델 등록 + 브랜치 자동 생성 + GitHub 라벨 생성 |
| `/wyh:model del <key>` | 모델 삭제 |
| `/wyh:user-mapping` | 협업자를 모델에 연결 |
| `/wyh:user-mapping del <github-user>` | 계정 매핑 해제 |
| `/wyh:add-project` | 프로젝트 등록 (alias + 폴더명) |
| `/wyh:add-project del <alias>` | 프로젝트 삭제 |

커맨드 파일 위치: `.claude/commands/`

---

## 자동화 (GitHub Actions)

### PR 유효성 검사 — `vibe-pr-validation.yml`

`vibe/*` 브랜치로 PR이 열릴 때마다 자동 실행됩니다.

| 검사 항목 | 실패 시 |
|-----------|---------|
| 등록된 모델 브랜치인지 | PR 자동 close |
| PR base가 모델 전용 브랜치인지 | PR 자동 close |
| PR 작성자가 등록된 계정인지 (계정 설정 시) | PR 자동 close |
| 작업 브랜치가 모델 브랜치에서 파생됐는지 | PR 자동 close |

실패 시 PR에 사유 코멘트가 자동 등록됩니다. 브랜치는 삭제되지 않습니다.

### 리뷰 파일 자동 배포 — `vibe-review-dispatch.yml`

`vibe/*` 브랜치로 PR이 **최초 생성**(`opened`)될 때 자동 실행됩니다.

- PR 브랜치명에서 모델 키와 이슈 번호 파싱
- `task/{project}/issue-{N}.md` 존재 여부로 참여 모델 파악
- PR 작성 모델을 제외한 나머지 모델 브랜치에 `task/{project}/review-{N}.md` 자동 push
