---
name: model
description: Vibe Eval 모델 추가/삭제. 이 프로젝트 전용. 사용법: /model add <key> | /model del <key>
---

# model — Vibe Eval 모델 관리

프로젝트 전용 명령어. `.github/vibe-models.json`을 기반으로 모델을 추가하거나 제거한다.

## 사용법

```
/model add <key>   — 새 모델 등록
/model del <key>   — 기존 모델 삭제
```

인수 없이 실행하면 위 사용법을 출력한다.

---

## /model add <key>

### 실행 순서

**1. 중복 확인**
`.github/vibe-models.json`을 읽어 `<key>`가 이미 존재하면 중단하고 안내한다.

**2. 추가 정보 수집 (순서대로 하나씩 묻는다)**
- `display_name`: 화면에 표시될 이름 (예: `Claude`, `Wyhill+지침서`)
- `label`: GitHub 라벨명 (기본값 제안: `model:<display_name>`)

**3. 요약 확인**
```
등록 예정 모델:
- key: <key>
- display_name: <display_name>
- label: <label>
- base_branch: <key>
- github_account: (미설정)

이대로 진행할까요?
```

**4. 확인 후 실행**

a. `.github/vibe-models.json`에 항목 추가:
```json
"<key>": {
  "display_name": "<display_name>",
  "github_account": "",
  "label": "<label>",
  "base_branch": "<key>"
}
```

b. 베이스 브랜치 생성 및 push (현재 브랜치가 main인지 확인 후 진행):
```bash
git checkout main
git push origin main:<key>
git checkout main
```

c. GitHub 라벨 생성 (이미 존재하면 건너뜀):
```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
gh label create "<label>" --color "d93f0b" --repo $REPO 2>/dev/null || echo "라벨 이미 존재"
```

d. `issue-body.md` 라벨 예시 줄 업데이트:
파일 내 `(예: ...)` 부분의 라벨 목록 끝에 `` `<label>` `` 추가.
대상 줄: `PR 라벨 필수: PR 생성 시 본인 모델명 라벨을 추가합니다 (예: ...)`

e. main 브랜치에 커밋 및 push:
```bash
git add .github/vibe-models.json .claude/skills/vibe-eval/templates/issue-body.md
git commit -m "feat: add vibe model <key> (<display_name>)"
git push origin main
```

**5. 완료 출력**
```
✅ 모델 등록 완료: <display_name> (<key>)
- 베이스 브랜치: <key> (생성됨)
- GitHub 라벨: <label> (생성됨)
- github_account: 미설정 → /user-mapping 으로 계정 연결 가능
```

---

## /model del <key>

### 실행 순서

**1. 존재 확인**
`.github/vibe-models.json`에 `<key>`가 없으면 중단하고 안내한다.

**2. 정보 표시 및 확인**
```
삭제 예정 모델:
- key: <key>
- display_name: <display_name>
- github_account: <github_account 또는 "미설정">

베이스 브랜치(<key>)도 삭제할까요? (yes/no)
정말 삭제하시겠습니까? (yes/no)
```

**3. 확인 후 실행**

a. `.github/vibe-models.json`에서 항목 제거.

b. `issue-body.md` 라벨 예시 줄에서 `` `<label>` `` 제거.

c. (yes 선택 시) 베이스 브랜치 삭제:
```bash
git push origin --delete <key>
```

d. main 브랜치에 커밋 및 push:
```bash
git add .github/vibe-models.json .claude/skills/vibe-eval/templates/issue-body.md
git commit -m "feat: remove vibe model <key> (<display_name>)"
git push origin main
```

**4. 완료 출력**
```
✅ 모델 삭제 완료: <display_name> (<key>)
```
