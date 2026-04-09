---
name: user-mapping
description: Vibe Eval GitHub 협업자↔모델 매핑/해제. 이 프로젝트 전용. 사용법: /user-mapping | /user-mapping del <github-user>
---

# user-mapping — Vibe Eval 사용자 매핑

프로젝트 전용 명령어. GitHub 협업자와 모델 계정을 연결하거나 해제한다.
모델 하나에 여러 계정을 등록할 수 있다.

## 사용법

```
/user-mapping                    — 협업자를 모델에 매핑 (대화형)
/user-mapping del <github-user>  — 매핑 해제
```

---

## /user-mapping (인수 없음)

### 실행 순서

**1. 협업자 목록 조회**
```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
gh api /repos/$REPO/collaborators --jq '.[].login'
```

**2. 미매핑 협업자 필터링**
`.github/vibe-models.json`을 읽어 모든 모델의 `github_accounts` 배열에 포함된 계정을 제외한 목록을 만든다.

**3. 미매핑 협업자 표시**
```
매핑되지 않은 협업자:
1. @username-a
2. @username-b
```
없으면 "매핑되지 않은 협업자가 없습니다." 출력 후 종료.

**4. 사용자 선택 요청**
"매핑할 사용자 번호를 선택하세요:"

**5. 전체 모델 목록 표시 (현재 등록된 계정 목록 함께)**
```
등록된 모델:
1. Claude (claude) — 계정 없음
2. Wyhill (wyhill) — @wyhill-a, @wyhill-b
3. Wyhill+지침서 (wyhill-guide) — 계정 없음
```

**6. 모델 선택 요청**
"연결할 모델 번호를 선택하세요:"
이미 계정이 있는 모델 선택 시: "현재 @{account1}, @{account2}가 연결되어 있습니다. 추가로 등록할까요?"

**7. 최종 확인**
```
@<github-user> → <key> (<display_name>) 에 추가
이대로 진행할까요?
```

**8. `.github/vibe-models.json` 업데이트**
해당 모델의 `github_accounts` 배열에 `<github-user>`를 추가한다 (중복 추가 방지).

**9. 커밋 및 push**
```bash
git add .github/vibe-models.json
git commit -m "chore: map @<github-user> to model <key>"
git push origin main
```

**10. 완료 출력**
```
✅ 매핑 완료: @<github-user> → <display_name> (<key>)
현재 등록된 계정: @<account1>, @<github-user>
```

---

## /user-mapping del <github-user>

### 실행 순서

**1. 매핑 확인**
`.github/vibe-models.json`에서 `github_accounts` 배열에 `<github-user>`가 포함된 모델을 찾는다.
없으면 "매핑된 모델이 없습니다." 출력 후 종료.

**2. 정보 표시 및 확인**
```
매핑 해제 예정:
@<github-user> → <display_name> (<key>)

계속할까요?
```

**3. `github_accounts` 배열에서 `<github-user>` 제거**

**4. 커밋 및 push**
```bash
git add .github/vibe-models.json
git commit -m "chore: unmap @<github-user> from model <key>"
git push origin main
```

**5. 완료 출력**
```
✅ 매핑 해제 완료: @<github-user> ← <display_name> (<key>)
남은 계정: @<account1> (또는 "없음")
```
