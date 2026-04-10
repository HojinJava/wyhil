# wyhil — Vibe Eval

> **W**hich AI model is best for **Y**our **H**uman-**I**n-the-**L**oop coding?

여러 AI 모델이 동일한 프롬프트로 바이브코딩(Vibe Coding)을 진행했을 때,  
각 모델의 결과를 구조화된 데이터로 기록하고 AI가 분석·비교 리포트를 생성하는 평가 시스템입니다.

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

## 흐름도

![vibe-eval 워크플로우](.claude/vibe_eval_workflow.svg)

---

## 참여 모델

> 각 모델은 각자 메인 브런치에서만 작업을 해야 됩니다.

<!-- VIBE-MODELS-START -->
| 모델 | 메인 브런치 | 등록된 사용자 |
|------|-----------|-------------|
| **Claude** | `claude` | @HojinJava |
| **Wyhill** | `wyhill` | - |
| **Wyhill+지침서** | `wyhill-guide` | @HojinJava |
| **안티그래비티** | `antigravity` | @myoungsuboh, @SeJin4019, @HojinJava |
| **Codex** | `codex` | @ChaeJ |
<!-- VIBE-MODELS-END -->

---

## 1. 이슈 생성 [관리자]

`/wyh:create-issue` 명령을 대화형으로 실행하여 이슈를 생성합니다.

```
/wyh:create-issue
```

이슈가 생성되면 각 참여 모델 브런치에 task 파일이 자동 생성됩니다.

> `task/{프로젝트명}/issue-{issueNum}.md`

---

## 2. 바이브 코딩 & 리뷰 진행 [사용자]

각 AI 모델에게 task 폴더 안의 md 파일을 순서대로 처리하도록 요청합니다.

```
task폴더안에 있는 md파일 모두 하나씩 진행해줘
```

> `issue-{N}.md` 가 있을 경우 → 바이브코딩 진행  
> `review-{N}.md` 가 있을 경우 → 리뷰 진행

각 AI 모델은 등록된 md파일에 의해 아래 작업을 순서대로 진행합니다:

1. 본인 메인 브런치로 이동
2. 해당 브런치에서 새로운 작업 브런치 생성
3. 새 브런치에서 작업 진행
4. 작업 후 본인 브런치에 PR 요청
5. 작업이 끝난 md파일을 `task_done/`으로 이동
   > `task/`에 남아 있으면 계속 중복 실행되기 때문에 이동 필수
6. md파일 이동분만 본인 메인 브런치에 push
   > `task/{프로젝트명}/issue-{N}.md` → `task_done/{프로젝트명}/issue-{N}.md`

---

## 3. 리뷰.md 자동 생성 [GitHub Actions]

PR 생성 요청이 오면 자동으로 유효성 검사를 진행합니다. **검증 실패 시 자동 Close.**

> **검증:** PR 타겟 브런치가 본인 메인 브런치인지 확인  
> 예) `claude` 모델은 `claude` 브런치에만 PR 요청 가능

검증을 통과하면 다른 모델 브런치에 리뷰 파일이 자동 생성됩니다.

> `task/{프로젝트명}/review-{PR Number}.md`

---

## 4. 사용자 확인 [사용자]

본인이 작업한 이슈가 PR에 잘 올라갔는지 확인합니다.

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

## 관리자 기능

Claude Code에서 `/명령어`로 실행하는 프로젝트 전용 관리 커맨드입니다.

| 커맨드 | 설명 |
|--------|------|
| `/wyh:create-issue` | 이슈 생성 마법사 (프로젝트 → 제목 → 레벨 → 프롬프트 → 모델) |
| `/wyh:bulk-create-issue <file.json>` | JSON 파일로 이슈 일괄 생성 |
| `/wyh:model` | 등록된 모델 목록 확인 |
| `/wyh:model add <key>` | 새 모델 등록 + 브랜치 자동 생성 + GitHub 라벨 생성 |
| `/wyh:model del <key>` | 모델 삭제 |
| `/wyh:user-mapping` | 협업자를 모델에 연결 |
| `/wyh:user-mapping del <github-user>` | 계정 매핑 해제 |
| `/wyh:add-project` | 프로젝트 등록 (alias + 폴더명) |
| `/wyh:add-project del <alias>` | 프로젝트 삭제 |

커맨드 파일 위치: `.claude/commands/wyh/`

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
