# wyhil — Vibe Eval

> **W**hich AI model is best for **Y**our **H**uman-**I**n-the-**L**oop coding?

여러 AI 모델이 동일한 프롬프트로 바이브코딩(Vibe Coding)을 진행했을 때,  
각 모델의 결과를 구조화된 데이터로 기록하고 AI가 분석·비교 리포트를 생성하는 평가 시스템입니다.

---

## 흐름도

![vibe-eval 워크플로우](.claude/vibe_eval_workflow.svg)

---

## 1. 프롬프트 작성

**담당:** 👤 관리자

모든 참여 AI 모델이 동일하게 수행할 공통 프롬프트를 작성합니다.  
`.claude/feature-definitions/{slug}.md` 파일에 저장하며, 이후 이슈 생성 시 자동으로 포함됩니다.

| 작성 항목 | 설명 |
|-----------|------|
| 공통 프롬프트 | 모든 모델이 수행할 작업 내용 |
| 평가지표 | 리뷰 시 기준이 되는 항목 |
| 프롬프트 레벨 | L1~L3 중 선택 |
| 참여 모델 | 평가에 참여할 AI 모델 목록 |

---

## 2. 이슈 생성

**담당:** 👤 관리자

작성된 프롬프트 파일을 기반으로 GitHub Issue를 생성합니다.  
이슈 하나가 하나의 평가 단위이며, 아래 내용이 자동으로 포함됩니다.

→ [이슈 바디 템플릿 보기](.claude/skills/vibe-eval/templates/issue-body.md)

| 포함 항목 | 설명 |
|-----------|------|
| 공통 프롬프트 | 모든 모델이 수행할 작업 |
| 평가지표 | 리뷰 시 기준이 되는 항목 |
| 작업 지시 파일 경로 | 각 모델 브랜치의 `{TARGET_DIR}/task/issue-{N}.md` 위치 안내 |

이슈 생성 시 참여 모델 각 브랜치에 `{TARGET_DIR}/task/issue-{N}.md`가 자동으로 push됩니다.

→ [작업 지시 템플릿 보기](.claude/skills/vibe-eval/templates/issue-task.md)

---

## 3. 바이브코딩 요청

**담당:** 🙋 사용자

각 AI 모델에게 본인 모델 브랜치의 작업 지시 파일을 읽게 합니다.

```
git pull origin {모델 브랜치}
# {TARGET_DIR}/task/issue-{N}.md 파일을 읽고 지시대로 개발해
```

| 규칙 | 내용 |
|------|------|
| 추가 프롬프트 | ❌ 금지 |
| 지침서 적용 | ✅ 허용 (LLM 사용정보에 기록) |

---

## 4. 바이브코딩 진행

**담당:** 🤖 바이브코딩 영역 (AI 모델)

이슈를 읽고 공통 프롬프트에 따라 개발을 진행한 후 PR을 생성합니다.

→ [작업 지시 템플릿 보기](.claude/skills/vibe-eval/templates/issue-task.md)

| 작업 항목 | 내용 |
|-----------|------|
| 브랜치 | `vibe/{model-slug}/{slug}-{issue-number}` |
| PR 제목 | `[{모델명}] {기능명} #{issue-number}` |
| PR → Issue 연결 | `References #{N}` 사용 (`Closes` 아님) |
| PR 생성 전 필수 | LLM 사용정보 작성 후 PR 본문 또는 세션 파일에 포함 |

---

## 5. 리뷰 요청

**담당:** 🙋 사용자

바이브코딩을 수행한 모델과 **다른** 모델에게 리뷰를 요청합니다.

```
{PR URL} 리뷰 진행해줘
```

---

## 6. 리뷰 진행

**담당:** 🤖 바이브코딩 영역 (AI 모델)

PR을 읽고 이슈의 평가지표를 기준으로 평가서를 작성한 뒤 PR 댓글로 등록합니다.

PR이 생성되면 리뷰어 모델 브랜치에 `{TARGET_DIR}/task/review-{N}.md`가 자동 push됩니다.

→ [리뷰 지시 템플릿 보기](.claude/skills/vibe-eval/templates/review-task.md)

| 작업 항목 | 내용 |
|-----------|------|
| 평가 기준 | 공통 프롬프트 이행·결과물 품질·자율 완성도 (각 1~5점) |
| 리뷰 방식 | PR diff만 기준 — 로컬 실행 없음 |
| 제출 방식 | PR 댓글로 평가서 등록 |
| 리뷰 완료 후 필수 | LLM 사용정보 작성 후 댓글에 포함 |

---

## 7. 리포트 생성

**담당:** 👤 관리자

모든 모델의 PR 및 리뷰가 완료되면 Issue를 수동으로 종료하고 리포트를 생성합니다.  
Issue에 연결된 PR과 세션 기록을 분석하여 모델별 비교 리포트를 작성합니다.

→ [리포트 생성 가이드](.claude/skills/vibe-eval/report.md)

결과물: `reports/{slug}-{issue-number}/comparison.md`

---

## 참여 모델

<!-- VIBE-MODELS-START -->
| 모델 | 브랜치 | 계정 |
|------|--------|------|
| Claude | `claude` | @HojinJava |
| Wyhill | `wyhill` | - |
| Wyhill+지침서 | `wyhill-guide` | @HojinJava |
| 안티그래비티 | `antigravity` | @myoungsuboh, @SeJin4019 |
| Codex | `codex` | - |
<!-- VIBE-MODELS-END -->

---

## 디렉터리 구조

```
repo/
├── {project}/                            ← 실제 프로젝트 코드
│   └── task/                             ← 이슈별 작업/리뷰 지시 파일 (모델 브랜치에 자동 push)
│       ├── issue-{N}.md                  ← AI 개발자용 작업 지시
│       └── review-{N}.md                 ← AI 리뷰어용 리뷰 지시
├── .gitignore
├── README.md
└── .claude/                              ← 평가 관련 파일 일체
    ├── commands/wyh/                     ← 관리자 전용 커맨드 (/wyh:*)
    │   ├── create-issue.md
    │   ├── add-project.md
    │   ├── model.md
    │   └── user-mapping.md
    ├── feature-definitions/              ← 공통 프롬프트 정의서
    │   └── {slug}.md
    ├── vibe-sessions/                    ← 모델별 세션 기록
    │   └── {slug}-{issue-number}/
    │       ├── claude.md
    │       ├── wyhill.md
    │       ├── wyhill-guide.md
    │       └── antigravity.md
    └── reports/                          ← 생성된 비교 리포트
        └── {slug}-{issue-number}/
            ├── claude.md
            ├── wyhill.md
            └── comparison.md
```

---

## 관리자 기능

Claude Code에서 `/명령어`로 실행하는 프로젝트 전용 관리 커맨드입니다.

| 커맨드 | 사용법 | 설명 |
|--------|--------|------|
| `/wyh:model` | `/wyh:model` | 등록된 모델 목록 및 브랜치 확인 |
| `/wyh:model add` | `/wyh:model add <key>` | 새 모델 등록 + 브랜치 자동 생성 + GitHub 라벨 생성 |
| `/wyh:model del` | `/wyh:model del <key>` | 모델 삭제 (브랜치 삭제 여부 선택 가능) |
| `/wyh:user-mapping` | `/wyh:user-mapping` | 협업자를 모델에 연결 |
| `/wyh:user-mapping del` | `/wyh:user-mapping del <github-user>` | 계정 매핑 해제 |
| `/wyh:add-project` | `/wyh:add-project` | 프로젝트 등록 (alias + 폴더명) |
| `/wyh:add-project del` | `/wyh:add-project del <alias>` | 프로젝트 삭제 |
| `/wyh:create-issue` | `/wyh:create-issue` | 이슈 생성 마법사 (프로젝트 선택 → 기능 제목 → 레벨 → 프롬프트 → 모델 선택) |

커맨드 파일 위치: `.claude/commands/wyh/`  
모델 정보는 `.github/vibe-models.json`에서 관리됩니다.

---

## 자동화 (GitHub Actions)

### PR 유효성 검사 — `vibe-pr-validation.yml`

`vibe/*` 브랜치로 PR이 열릴 때마다 자동 실행됩니다.

| 검사 항목 | 내용 | 실패 시 |
|-----------|------|---------|
| 모델 등록 여부 | 브랜치의 모델 키가 `vibe-models.json`에 등록되어 있는지 | PR 자동 close |
| 대상 브랜치 | PR base가 해당 모델의 전용 브랜치인지 (`vibe/claude/...` → `claude`) | PR 자동 close |
| 작성자 계정 | PR 작성자가 해당 모델에 등록된 계정인지 (계정 미설정 시 생략) | PR 자동 close |
| 브랜치 파생 관계 | 작업 브랜치가 모델 브랜치를 ancestor로 갖는지 | PR 자동 close |

실패 시 PR에 사유 코멘트가 자동으로 등록되고, PR Checks에 ❌ 로 표시됩니다.  
브랜치는 삭제되지 않으며 수정 후 재시도할 수 있습니다.

### 리뷰 파일 자동 배포 — `vibe-review-dispatch.yml`

`vibe/*` 브랜치로 PR이 **최초 생성**(`opened`)될 때 자동 실행됩니다.

- PR 브랜치명에서 모델 키와 이슈 번호를 파싱
- 해당 이슈의 참여 모델을 `{TARGET_DIR}/task/issue-{N}.md` 존재 여부로 파악
- PR 작성 모델을 제외한 나머지 참여 모델 브랜치에 `review-{N}.md` 자동 push

> 계정 등록은 `/wyh:user-mapping`, 모델 등록은 `/wyh:model add` 커맨드를 사용합니다.