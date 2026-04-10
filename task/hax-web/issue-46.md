# hax-web/jpa_to_mysql [L3] — 작업 지시 (#46)

## 지금 바로 실행

git fetch origin {모델명}
git checkout -b vibe/{모델명}/hax-web-jpa_to_mysql-46 origin/{모델명}

위 명령 실행 후 hax-web 폴더 안에서 작업을 진행한다.

## 해야 할 작업

공통 프롬프트는 .claude/feature-definitions/hax-web-jpa_to_mysql.md 참고

## 반드시 지켜야 할 규칙

작업 완료 후 모든 커밋을 1개로 squash한다.
squash 후 위에서 checkout한 {모델명} 브랜치를 대상으로 PR을 생성한다.
PR 본문에 `References #46` 를 반드시 포함한다.
PR에 본인 모델 라벨을 추가한다.

PR 올리기 전 Commits 탭에서 본인 커밋이 1개인지 확인한다.
여러 개라면 main에서 브랜치를 딴 것이므로 PR을 닫고 처음부터 다시 시작한다.

## PR 본문 필수 항목

모델명:
소요 시간:
토큰 수: (예: 입력 12,000 / 출력 3,500)
프롬프트 횟수:
추가 프롬프트: (없으면 없음)
수동 수정: (없으면 없음)
지침서: (없으면 없음)

## 작업 완료 후 파일 정리

PR 생성이 완료되면 이 파일을 `task_done/` 폴더로 이동한다.  
**작업 브랜치와 모델 베이스 브랜치 모두** 정리해야 한다.

**1. 현재 작업 브랜치에서 이동**

```
mkdir -p task_done/hax-web
git mv task/hax-web/issue-46.md task_done/hax-web/issue-46.md
git commit -m "chore: move issue-46 task to task_done"
git push origin vibe/{모델명}/hax-web-jpa_to_mysql-46
```

**2. 모델 베이스 브랜치에서도 이동**

```
git checkout {모델명}
git pull origin {모델명}
mkdir -p task_done/hax-web
git mv task/hax-web/issue-46.md task_done/hax-web/issue-46.md
git commit -m "chore: move issue-46 task to task_done"
git push origin {모델명}
```