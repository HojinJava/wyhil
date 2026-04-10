---
name: wyh:add-project
description: Vibe Eval 프로젝트 추가/삭제. 이 프로젝트 전용. 사용법: /wyh:add-project | /wyh:add-project del <alias>
---

# wyh:add-project — Vibe Eval 프로젝트 관리

프로젝트 전용 명령어. `.github/vibe-projects.json`을 기반으로 프로젝트를 추가하거나 제거한다.
프로젝트는 `/wyh:create-issue` 이슈 생성 시 선택 목록에 표시된다.

## 사용법

```
/wyh:add-project              — 등록된 프로젝트 목록 출력
/wyh:add-project del <alias>  — 프로젝트 삭제
```

인수 없이 실행하면 현재 등록된 프로젝트 목록을 출력한다:

```
등록된 프로젝트:
1. mall — 폴더: mall/
2. socket — 폴더: supersocket/
3. card — 폴더: cardstackview/
```

이후 "새 프로젝트를 추가할까요? (yes/no)" 로 추가 여부를 묻는다.

---

## /add-project (인수 없음 또는 add 흐름)

### 실행 순서

**1. 프로젝트 목록 출력**
`.github/vibe-projects.json`을 읽어 등록된 프로젝트를 출력한다.

**2. alias 입력**
"프로젝트 alias를 입력하세요. (예: `mall`, `socket`, `myapp`)"

- 이미 존재하는 alias면 중단하고 안내한다.
- 입력값을 ALIAS로 저장.

**3. 폴더명 입력**
기본값으로 alias를 제안한다:

```
레포 내 상위 폴더명을 입력하세요:
1. {ALIAS}/  (기본값)
2. 직접 입력
```

- 1 선택 시 FOLDER = ALIAS
- 2 선택 시 직접 입력받아 저장
- 입력값을 FOLDER로 저장.

**4. 최종 확인**
```
등록 예정 프로젝트:
- alias:  {ALIAS}
- 폴더:   {FOLDER}/

1. 진행
2. 취소
3. alias 수정
4. 폴더 수정
```

**5. `.github/vibe-projects.json` 업데이트**
```json
"{ALIAS}": {
  "alias": "{ALIAS}",
  "folder": "{FOLDER}"
}
```

**6. 커밋 및 push**
```bash
git add .github/vibe-projects.json
git commit -m "chore: add project {ALIAS} (folder: {FOLDER})"
git push origin main
```

**7. 완료 출력**
```
✅ 프로젝트 등록 완료: {ALIAS} (폴더: {FOLDER}/)
이슈 생성 시 /wyh:create-issue 에서 선택 가능합니다.
```

---

## /add-project del <alias>

### 실행 순서

**1. 존재 확인**
`.github/vibe-projects.json`에 `<alias>`가 없으면 중단하고 안내한다.

**2. 정보 표시 및 확인**
```
삭제 예정 프로젝트:
- alias: <alias>
- 폴더:  <folder>/

1. 진행
2. 취소
```

**3. `.github/vibe-projects.json`에서 항목 제거**

**4. 커밋 및 push**
```bash
git add .github/vibe-projects.json
git commit -m "chore: remove project <alias>"
git push origin main
```

**5. 완료 출력**
```
✅ 프로젝트 삭제 완료: <alias>
```
