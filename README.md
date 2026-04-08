# wyhil — Vibe Eval

> **W**hich AI model is best for **Y**our **H**uman-**I**n-the-**L**oop coding?

여러 AI 모델이 동일한 프롬프트로 바이브코딩(Vibe Coding)을 진행했을 때,  
각 모델의 결과를 구조화된 데이터로 기록하고 AI가 분석·비교 리포트를 생성하는 평가 시스템입니다.

---

## 흐름도

```
┌─────────────────────────────────────────────────────────────────┐
│                         관리자 (Admin)                           │
│                                                                  │
│  1. feature-definitions/{slug}.md  작성 (공통 프롬프트)          │
│  2. /vibe-eval create feature.md  실행                           │
│     └─ GitHub Issue 생성  →  {프로젝트}/{기능} [L1~L3]           │
│     └─ 모델별 세션 파일 템플릿 생성                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴──────────┐
                    │    Issue #{N}       │
                    │ (공통 프롬프트 +    │
                    │  모델별 작업 규칙)  │
                    └─────────┬──────────┘
                              │
              이슈 URL 전달 → 각 모델이 읽고 시작
                              │
          ┌───────────────────┼───────────────────────────┐
          │                   │                           │
          ▼                   ▼                           ▼
   ┌────────────┐    ┌──────────────────┐      ┌──────────────────┐
   │   Claude   │    │     Wyhill       │  ··· │   Antigravity    │
   └─────┬──────┘    └────────┬─────────┘      └────────┬─────────┘
         │                    │                          │
         ▼                    ▼                          ▼
   브랜치 생성            코드 작성               세션 기록 작성
   vibe/{model}/        (공통 프롬프트 기준)   vibe-sessions/
   {slug}-{N}                                  {slug}-{N}/{model}.md
         │                    │                          │
         └────────────────────┴──────────────────────────┘
                              │
                           PR 생성
                    제목: [{모델}] {기능명} #{N}
                    본문: References #{N}
                              │
                    Issue ← PR ← Branch
               (PR에서 이슈 연결, Closes 아님)
                              │
                    모든 모델 PR 완료
                    관리자가 Issue 수동 종료
                              │
                    ┌─────────┴──────────┐
                    │  /vibe-eval report  │
                    │  비교 리포트 생성   │
                    └────────────────────┘
```

### 핵심 규칙

| 항목 | 내용 |
|------|------|
| **Issue** | 평가 단위 — 공통 프롬프트 + 모든 모델의 브랜치·PR 규칙 포함 |
| **바이브코딩 프롬프트** | 이슈 URL 하나만 전달 (추가 프롬프트 원칙적 금지) |
| **지침서** | 모델 본인이 로컬에서 자율 적용 가능 (세션 기록에 명시) |
| **브랜치 네이밍** | `vibe/{model-slug}/{slug}-{issue-number}` |
| **PR 제목** | `[{모델명}] {기능명} #{issue-number}` |
| **PR → Issue 연결** | `References #{N}` 사용 (`Closes` 아님) |
| **Issue 종료** | 모든 모델 PR merge 후 관리자가 수동으로 닫음 |
| **세션 기록** | `vibe-sessions/{slug}-{N}/{model}.md` — PR 전 필수 작성 |

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

## AI 모델에게 전달하는 방법

이슈 URL만 전달하면 됩니다. 이슈 본문에 공통 프롬프트·브랜치 규칙·PR 규칙이 모두 포함되어 있습니다.

```
https://github.com/HojinJava/wyhil/issues/42 해당 이슈 확인 후 진행해
```

> 모델이 이슈를 읽고 본인 모델에 해당하는 브랜치를 생성 후 작업을 시작합니다.  
> 추가 설명 없이 이슈 URL 하나로 전체 작업이 완료되어야 합니다.

---

## 프롬프트 레벨 (L1 ~ L3)

| 레벨 | 수준 | 예시 |
|------|------|------|
| **L1** | 단순 지시 | "로그인 만들어줘" |
| **L2** | 조건부 지시 | "회원 테이블 있는지 확인하고, 있으면 로그인 구현, 없으면 테이블부터 만들어" |
| **L3** | 흐름 포함 지시 | "탈퇴 기능 있는지 체크하고, 없으면 만들고, 있으면 로그인과 연동해서 전체 흐름 완성해" |

---

## 디렉터리 구조

```
repo/
├── {project}/                            ← 실제 프로젝트 코드
├── feature-definitions/                  ← 공통 프롬프트 정의서
│   └── {slug}.md
├── vibe-sessions/                        ← 모델별 세션 기록
│   └── {slug}-{issue-number}/
│       ├── claude.md
│       ├── wyhill.md
│       ├── wyhill-guide.md
│       ├── cortex-code.md
│       └── antigravity.md
└── reports/                              ← 생성된 비교 리포트
    └── {slug}-{issue-number}/
        ├── claude.md
        ├── wyhill.md
        └── comparison.md
```

### 슬러그 & 네이밍 규칙

```
기능 제목:  mall/codebase-analysis [L2]
slug:       mall-codebase-analysis
Issue #:    1
combined:   mall-codebase-analysis-1

브랜치:  vibe/claude/mall-codebase-analysis-1
PR 제목: [Claude] mall/codebase-analysis #1
세션:    vibe-sessions/mall-codebase-analysis-1/claude.md
```

---

## 세션 기록 항목

바이브코딩 완료 후 `vibe-sessions/{combined-slug}/{model}.md`에 기록합니다.

| 항목 | 설명 |
|------|------|
| 태스크 | 이슈 제목 |
| 모델명 | 사용한 AI 모델 |
| 소요 시간 | 작업 총 시간 |
| 프롬프트 횟수 | 총 입력 횟수 |
| 인풋/아웃풋 토큰 | 사용 토큰 수 (확인 불가 시 N/A) |
| 추가 프롬프트 | 세션 중 발생한 추가 지시 (없으면 "없음") |
| 수동 수정 여부 | AI 결과물을 사람이 직접 수정했는지 여부 |
| 지침서 | 로컬에서 사용한 지침서 (없으면 "없음") |
| 자체 완성도 | 1~5점 자체 평가 |

---

## 스킬 명령어

| 명령어 | 설명 |
|--------|------|
| `/vibe-eval create feature.md` | 기능 정의서로 Issue + 세션 템플릿 자동 생성 |
| `/vibe-eval report {slug}-{N}` | 세션 데이터 분석 후 비교 리포트 생성 |
| `/vibe-eval revise {slug}-{N}` | 수정 버전 새 Issue 생성 (기존 이슈에 대체 안내 코멘트) |

---

## 설계 문서

- [Vibe Eval 설계 문서](docs/superpowers/specs/2026-04-08-vibe-eval-design.md)
