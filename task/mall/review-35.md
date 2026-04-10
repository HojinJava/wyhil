# mall/test_11 — 리뷰 지시 (#35)

## 리뷰할 PR

PR #44 — Claude 모델 작업물

아래 명령으로 PR 내용을 확인한다.
gh pr view 44 --repo HojinJava/wyhil

## 평가 기준

- 공통 프롬프트 이행 (1~5): 요구사항을 빠짐없이 수행했는가
- 결과물 품질 (1~5): 코드 가독성·구조·예외처리
- 자율 완성도 (1~5): 추가 프롬프트·수동 수정 없이 완료했는가

리뷰는 PR diff만 기준으로 한다. 코드를 로컬에서 실행하지 않는다.

## 제출 방식

평가 완료 후 아래 양식을 PR #44 댓글로 등록한다.

종합 평점: /5
공통 프롬프트 이행 (1~5):
결과물 품질 (1~5):
자율 완성도 (1~5):

잘된 점:
아쉬운 점:

모델명:
소요 시간:
토큰 수: (예: 입력 12,000 / 출력 3,500)
프롬프트 횟수:
지침서: (없으면 없음)

## 리뷰 완료 후 파일 정리

PR 댓글 등록이 완료되면 이 파일을 `task_done/` 폴더로 이동한다.

```
mkdir -p task_done/mall
git mv task/mall/review-35.md task_done/mall/review-35.md
git commit -m "chore: move review-35 task to task_done"
git push origin wyhill-guide
```
