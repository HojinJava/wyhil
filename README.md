# wyhil — Vibe Eval

> **W**hich AI model is best for **Y**our **H**uman-**I**n-the-**L**oop coding?

여러 AI 모델이 동일한 프롬프트로 바이브코딩(Vibe Coding)을 진행했을 때, 각 모델의 결과를 구조화된 데이터로 기록하고 AI가 분석·비교 리포트를 생성하는 평가 시스템입니다.

---

## 핵심 아이디어

같은 프롬프트를 주었을 때 Claude, GPT-4, Gemini 등 각 AI 모델이 어떻게 다르게 구현하는지 GitHub 기반으로 추적하고 비교합니다.

```
동일한 프롬프트
    ├── Claude Sonnet → 구현 A
    ├── GPT-4o        → 구현 B
    └── Gemini Pro    → 구현 C
                           ↓
                     비교 리포트 생성
```

---

## 워크플로우

### 1단계: 마일스톤 설정
`feature.md`(기능 정의서)를 작성하고 Claude Code 스킬로 GitHub 셋업을 자동화합니다.

```bash
/vibe-eval create feature.md
```

- GitHub **Milestone** 생성 (`frontend/user-auth [L2]` 형식)
- 참여 모델별 **Issue** 자동 생성
- 세션 기록용 MD 템플릿 파일 자동 생성

### 2단계: 바이브코딩
각 참여자가 담당 Issue를 확인하고 자신의 AI 모델로 바이브코딩을 진행합니다.

- 담당 Issue의 공통 프롬프트 기준으로 작업
- 세션 종료 후 `vibe-sessions/{milestone}/{model}.md` 작성 (사용 토큰, 추가 프롬프트, 지침서 등 기록)
- PR 생성 후 Issue 연결

### 3단계: 리포트 생성
```bash
/vibe-eval report frontend-user-auth
```

- 모델별 **개별 리포트** 생성
- 전체 **비교 리포트** 생성 (표 + AI 종합 분석)

---

## GitHub 구조

```
repo/
├── {project}/                        ← 실제 프로젝트 코드
├── feature-definitions/              ← 기능 정의서 (공통 프롬프트)
│   └── {milestone-slug}.md
├── vibe-sessions/                    ← 세션 기록 (바이브코딩 후 작성)
│   └── {milestone-slug}/
│       ├── claude-sonnet-4.5.md
│       ├── gpt-4o.md
│       └── gemini-pro.md
└── reports/                          ← 생성된 리포트
    └── {milestone-slug}/
        ├── claude-sonnet-4.5.md
        ├── gpt-4o.md
        └── comparison.md
```

### Milestone 네이밍 규칙

```
{프로젝트}/{기능명} [L{레벨}]

예시:
  frontend/user-auth [L2]
  backend/api-auth [L3]
```

### 프롬프트 레벨 (L1 ~ L3)

| 레벨 | 수준 | 예시 |
|------|------|------|
| L1 | 단순 지시 | "로그인 만들어줘" |
| L2 | 조건부 지시 | "회원 테이블 있는지 확인하고, 있으면 로그인 구현, 없으면 테이블부터 만들어" |
| L3 | 흐름 포함 지시 | "탈퇴 기능 있는지 체크하고, 없으면 만들고, 있으면 로그인과 연동해서..." |

---

## 세션 기록 포맷

바이브코딩 세션 종료 후 `vibe-sessions/{milestone}/{model}.md`에 기록:

| 항목 | 설명 |
|------|------|
| 모델명 / 제공사 | 사용한 AI 모델 |
| 소요 시간 | 작업 총 시간 |
| 프롬프트 횟수 | 총 몇 번 프롬프트를 입력했는지 |
| 인풋/아웃풋 토큰 | 사용 토큰 수 (확인 불가 시 N/A) |
| 공통 프롬프트 | 마일스톤 기준 프롬프트 |
| 추가 프롬프트 | 세션 중 발생한 추가 지시 목록 |
| 수동 수정 여부 | AI 결과물을 사람이 직접 수정했는지 |
| 지침서 | 로컬에서 사용한 지침서 파일 (CLAUDE.md 등) |
| 자체 완성도 | 1~5점 자체 평가 |

---

## 스킬 명령어

| 명령어 | 설명 |
|--------|------|
| `/vibe-eval create feature.md` | 기능 정의서로 Milestone + Issue 자동 생성 |
| `/vibe-eval report {milestone-slug}` | 세션 데이터 분석 후 리포트 생성 |
| `/vibe-eval revise {milestone-slug}` | 마일스톤 수정 버전 생성 (이전 버전 참조 유지) |

---

## 설계 문서

- [Vibe Eval 설계 문서](docs/superpowers/specs/2026-04-08-vibe-eval-design.md)
