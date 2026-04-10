---
name: wyh:user-mapping
description: Vibe Eval GitHub 협업자↔모델 매핑/해제. 이 프로젝트 전용. 사용법: /wyh:user-mapping | /wyh:user-mapping del <github-user>
---

# wyh:user-mapping — Vibe Eval 사용자 매핑

프로젝트 전용 명령어. GitHub 협업자와 모델 계정을 연결하거나 해제한다.
모델 하나에 여러 계정을 등록할 수 있다.

## 사용법

```
/wyh:user-mapping                    — 협업자를 모델에 매핑 (대화형)
/wyh:user-mapping del <github-user>  — 매핑 해제
```

---

## /wyh:user-mapping (인수 없음)

### 실행 순서

**1. 협업자 목록 조회**

Windows Git bash에서 `/repos/...` 형태로 호출하면 경로가 파일시스템 경로로 재작성되므로
반드시 앞의 슬래시를 생략하고 호출한다.

```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
gh api "repos/$REPO/collaborators" --jq '.[].login'
```

**2. 협업자 목록 표시**

한 사용자가 여러 모델에 동시에 매핑될 수 있으므로 이미 매핑된 계정도 포함해 전체 협업자를 표시한다.
각 계정 옆에 현재 연결된 모델 목록을 괄호로 보여준다.

```
협업자 목록:
1. @HojinJava  (현재: claude)
2. @username-b (현재: 없음)
```

협업자가 없으면 "등록된 협업자가 없습니다." 출력 후 종료.

"매핑할 사용자 번호를 선택하세요:"

**3. 전체 모델 목록 표시 및 선택**
```
등록된 모델:
1. Claude (claude) — 계정: @HojinJava
2. Wyhill (wyhill) — 계정: 없음
3. Wyhill+지침서 (wyhill-guide) — 계정: 없음
```

이미 해당 사용자가 연결된 모델에는 `(이미 연결됨)` 표시.

"연결할 모델 번호를 선택하세요:"

이미 연결된 모델 선택 시 "이미 해당 모델에 연결되어 있습니다." 출력 후 다시 선택 요청.

**4. 최종 확인**
```
@<github-user> → <key> (<display_name>) 에 추가

1. 진행
2. 취소
```

**5. `.github/vibe-models.json` 업데이트**
해당 모델의 `github_accounts` 배열에 `<github-user>`를 추가한다 (중복 추가 방지).

**6. README.md 참여 모델 표 갱신** (→ model.md의 README 싱크 규칙 참고)

**7. 커밋 및 push**

워킹 트리에 미커밋 변경사항이 있을 수 있으므로 stash로 임시 저장 후 pull한다:

```bash
git add .github/vibe-models.json README.md
git commit -m "chore: map @<github-user> to model <key>"
STASH_RESULT=$(git stash --include-untracked 2>&1)
git pull --rebase origin main
echo "$STASH_RESULT" | grep -q "No local changes to stash" || git stash pop
git push origin main
```

**8. 완료 출력**
```
✅ 매핑 완료: @<github-user> → <display_name> (<key>)
현재 등록된 계정: @<account1>, @<github-user>
```

---

## /wyh:user-mapping del <github-user>

### 실행 순서

**1. 매핑 확인**
`.github/vibe-models.json`에서 `github_accounts` 배열에 `<github-user>`가 포함된 모델을 찾는다.
없으면 "매핑된 모델이 없습니다." 출력 후 종료.

**2. 정보 표시 및 확인**
```
매핑 해제 예정:
@<github-user> → <display_name> (<key>)

1. 진행
2. 취소
```

**3. `github_accounts` 배열에서 `<github-user>` 제거**

**4. README.md 참여 모델 표 갱신** (→ model.md의 README 싱크 규칙 참고)

**5. 커밋 및 push**

```bash
git add .github/vibe-models.json README.md
git commit -m "chore: unmap @<github-user> from model <key>"
STASH_RESULT=$(git stash --include-untracked 2>&1)
git pull --rebase origin main
echo "$STASH_RESULT" | grep -q "No local changes to stash" || git stash pop
git push origin main
```

**5. 완료 출력**
```
✅ 매핑 해제 완료: @<github-user> ← <display_name> (<key>)
남은 계정: @<account1> (또는 "없음")
```
