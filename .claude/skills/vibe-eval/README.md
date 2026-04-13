# vibe-eval 스킬

바이브코딩 AI 모델 비교 평가 시스템.

## 설치

```bash
cp -r skills/vibe-eval ~/.claude/skills/vibe-eval
```

## 사전 요구사항

- Claude Code CLI
- GitHub CLI (`gh`) — 인증 완료 상태
- 실행 전 레포 루트에서 `gh auth status` 확인

## 명령어

| 명령어 | 설명 |
|--------|------|
| `/vibe-eval create feature.md` | Milestone + 모델별 Issue 생성 |
| `/vibe-eval report {milestone-slug}` | 세션 분석 리포트 생성 |
| `/vibe-eval revise {milestone-slug}` | 마일스톤 수정 버전 생성 |

## 템플릿 수정 시 주의

`templates/` 하위 파일을 수정할 때는 **기존에 브랜치에 배포된 이슈 파일(`task/**/issue-*.md`)과 GitHub 이슈 본문을 절대 변경하지 않는다.**
수정된 템플릿은 이후 새로 생성되는 이슈에만 적용된다.

## feature.md 작성법

`feature-definitions/example-feature.md` 참고
