---
name: wyh:model
description: Vibe Eval 모델 추가/삭제. 이 프로젝트 전용. 사용법: /wyh:model add <key> | /wyh:model del <key>
---

# wyh:model — Vibe Eval 모델 관리

프로젝트 전용 명령어. `.github/vibe-models.json`을 기반으로 모델을 추가하거나 제거한다.

## 사용법

```
/wyh:model add <key>   — 새 모델 등록
/wyh:model del <key>   — 기존 모델 삭제
```

인수 없이 실행하면 현재 등록된 모델 목록을 출력한다:

```
등록된 모델:
1. Claude (claude) — 브랜치: claude — 계정: 없음
2. Wyhill (wyhill) — 브랜치: wyhill — 계정: @wyhill-a, @wyhill-b
3. Wyhill+지침서 (wyhill-guide) — 브랜치: wyhill-guide — 계정: 없음
```

---

## /wyh:model add <key>

### 실행 순서

**1. 중복 확인**
`.github/vibe-models.json`을 읽어 `<key>`가 이미 존재하면 중단하고 안내한다.

**2. display_name 입력**
"화면에 표시될 이름을 입력하세요. (예: `Claude`, `Wyhill+지침서`)"

자유 입력값을 DISPLAY_NAME으로 저장.

**3. label 입력**
기본값을 제안하고 번호로 선택받는다:

```
GitHub 라벨명을 선택하세요:
1. model:<display_name>  (기본값)
2. 직접 입력
```

- 1 선택 시 `model:<display_name>` 사용
- 2 선택 시 직접 입력받아 저장

**4. 요약 확인**
```
등록 예정 모델:
- key:          <key>
- display_name: <display_name>
- label:        <label>
- base_branch:  <key>  ← 브랜치도 즉시 생성됩니다
- github_accounts: (미설정)

1. 진행
2. 취소
3. display_name 수정
4. label 수정
```

**5. 확인 후 실행**

a. 베이스 브랜치 즉시 생성 및 push:
```bash
git push origin main:<key>
```
로컬 브랜치 없이 remote에 바로 생성한다. 이미 존재하면 건너뜀.

b. `.github/vibe-models.json`에 항목 추가:
```json
"<key>": {
  "display_name": "<display_name>",
  "github_accounts": [],
  "label": "<label>",
  "base_branch": "<key>"
}
```

c. GitHub 라벨 생성 (이미 존재하면 건너뜀):
```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
gh label create "<label>" --color "d93f0b" --repo $REPO 2>/dev/null || echo "라벨 이미 존재"
```

d. README.md 참여 모델 표 갱신 (→ README 싱크 규칙 참고)

e. main 브랜치에 커밋 및 push:
```bash
git add .github/vibe-models.json README.md
git commit -m "feat: add vibe model <key> (<display_name>)"
STASH_RESULT=$(git stash --include-untracked 2>&1)
git pull --rebase origin main
echo "$STASH_RESULT" | grep -q "No local changes to stash" || git stash pop
git push origin main
```

**6. 완료 출력**
```
✅ 모델 등록 완료: <display_name> (<key>)
- 베이스 브랜치: <key> (생성됨)
- GitHub 라벨: <label> (생성됨)
- github_accounts: 미설정 → /user-mapping 으로 계정 연결 가능
```

---

## /wyh:model del <key>

### 실행 순서

**1. 존재 확인**
`.github/vibe-models.json`에 `<key>`가 없으면 중단하고 안내한다.

**2. 정보 표시 및 브랜치 삭제 여부 확인**
```
삭제 예정 모델:
- key:          <key>
- display_name: <display_name>
- base_branch:  <key>
- github_accounts: <계정 목록 또는 "미설정">

베이스 브랜치(<key>)도 삭제할까요?
1. 예
2. 아니오
```

**3. 최종 확인**
```
정말 삭제하시겠습니까?
1. 예, 삭제합니다
2. 아니오, 취소합니다
```

**4. 확인 후 실행**

a. `.github/vibe-models.json`에서 항목 제거.

b. (브랜치 삭제 선택 시) 베이스 브랜치 삭제:
```bash
git push origin --delete <key>
```

c. README.md 참여 모델 표 갱신 (→ README 싱크 규칙 참고)

d. main 브랜치에 커밋 및 push:
```bash
git add .github/vibe-models.json README.md
git commit -m "feat: remove vibe model <key> (<display_name>)"
STASH_RESULT=$(git stash --include-untracked 2>&1)
git pull --rebase origin main
echo "$STASH_RESULT" | grep -q "No local changes to stash" || git stash pop
git push origin main
```

**5. 완료 출력**
```
✅ 모델 삭제 완료: <display_name> (<key>)
```

---

## README 싱크 규칙

`/wyh:model add`, `/wyh:model del`, `/wyh:user-mapping`, `/wyh:user-mapping del` 실행 후 커밋 전에 반드시 수행한다.

`.github/vibe-models.json`을 읽어 아래 형식으로 표를 생성하고,
`README.md`의 `<!-- VIBE-MODELS-START -->` ~ `<!-- VIBE-MODELS-END -->` 사이를 교체한다.

```markdown
<!-- VIBE-MODELS-START -->
| 모델 | 브랜치 | 계정 |
|------|--------|------|
| {display_name} | `{base_branch}` | {계정 목록 또는 -} |
<!-- VIBE-MODELS-END -->
```

- `github_accounts`가 비어있으면 `-`
- 계정이 있으면 `@account1, @account2` 형식
- 모델 순서는 JSON 파일 순서 유지
