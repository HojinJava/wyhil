# /vibe-eval report

완료된 바이브코딩 세션을 분석하여 개별 리포트와 비교 리포트를 생성한다.

## 트리거

사용자가 `/vibe-eval report {combined-slug}` 를 실행할 때.

`combined-slug` 형식: `{slug}-{issue-number}` (예: `mall-codebase-analysis-1`)

## 사전 확인

1. `vibe-sessions/{COMBINED_SLUG}/` 디렉토리가 존재하고 MD 파일이 1개 이상인지 확인
   - 없으면: "❌ `vibe-sessions/{COMBINED_SLUG}/` 에 세션 파일이 없습니다." 출력 후 종료
2. REPO = `gh repo view --json nameWithOwner -q .nameWithOwner`
3. ISSUE_NUMBER: COMBINED_SLUG의 마지막 `-` 뒤 숫자 (예: `1`)

## 실행 절차

### 1단계: 세션 파일 수집

`vibe-sessions/{COMBINED_SLUG}/` 의 모든 `.md` 파일을 읽는다.

각 파일에서 다음 값을 파싱한다:
- **모델명**: `- **모델:**` 뒤의 값
- **태스크**: `- **태스크:**` 뒤의 값 → TITLE로 사용
- **날짜**: `- **날짜:**` 뒤의 값
- **소요 시간**: `- **소요 시간:**` 뒤의 값 (분 단위)
- **지침서**: `- **지침서:**` 뒤의 값
- **프롬프트 횟수**: `- **총 프롬프트 횟수:**` 뒤의 값
- **인풋 토큰**: `- **총 인풋 토큰:**` 뒤의 값 (N/A 허용)
- **아웃풋 토큰**: `- **총 아웃풋 토큰:**` 뒤의 값 (N/A 허용)
- **추가 프롬프트**: `### 추가 프롬프트` 섹션의 번호 목록
- **수동 수정 여부**: `- **수동 수정 여부:**` 뒤의 값
- **수정 내용**: `- **수정 내용:**` 뒤의 값
- **완성도**: `- **완성도:**` 뒤의 값 (숫자/5 형식)
- **메모**: `- **메모:**` 뒤의 값
- **Issue 번호**: `- **Issue:** #` 뒤의 숫자
- **PR**: `- **PR:**` 뒤의 값 (번호 또는 "미연결")
- **브랜치**: `- **브랜치:**` 뒤의 값

LEVEL은 TITLE의 `[L{n}]`에서 추출.

### 2단계: PR diff 수집

각 세션의 PR 값이 숫자인 경우:
```bash
gh pr diff {PR_NUMBER}
```
diff를 분석하여:
- 변경 파일 수
- 추가 라인 수 (+)
- 삭제 라인 수 (-)
- 주요 변경 경로 (상위 5개 파일)

PR이 "미연결"이면: 코드 변경 분석 항목에 `[PR 미연결]` 표시.

### 3단계: 개별 리포트 생성

각 세션 파일에 대해 `reports/{COMBINED_SLUG}/{MODEL_FILENAME}.md` 를 생성한다.

```markdown
# 세션 분석: {MODEL} × {TITLE}

**생성일:** {TODAY}

## 요약
- 완성도: {SCORE}/5
- 총 프롬프트: {PROMPT_COUNT}회 (추가 {ADDITIONAL_COUNT}회)
- 인풋 토큰: {INPUT_TOKENS} / 아웃풋 토큰: {OUTPUT_TOKENS}
- 소요 시간: {DURATION}분
- 수동 수정: {MANUAL_FIX}
- 지침서: {GUIDELINES}

## 코드 변경 분석
<!-- PR 미연결 시 [PR 미연결] 표시 -->
- 변경 파일 수: {CHANGED_FILES}개
- 추가 라인: +{ADDED} / 삭제 라인: -{DELETED}
- 주요 변경: {MAIN_PATHS}

## AI 분석

세션 데이터와 코드 변경을 바탕으로 다음을 분석한다:

- **공통 프롬프트 구현율**: 요구사항 중 몇 %가 구현되었는가. 미구현 항목을 구체적으로 명시.
- **추가 프롬프트 대응**: 추가 프롬프트 {ADDITIONAL_COUNT}개 중 몇 개를 올바르게 처리했는가.
- **코드 품질 소견**: diff를 보고 코드 구조, 패턴, 주목할 점.
- **총평**: 이 세션의 강점과 약점. L{LEVEL} 레벨 프롬프트에 대한 모델의 대응 방식.
```

### 4단계: 비교 리포트 생성 (세션 2개 이상인 경우)

세션 파일이 2개 이상일 때만 `reports/{COMBINED_SLUG}/comparison.md` 를 생성한다.

```markdown
# 비교 분석: {TITLE}

**생성일:** {TODAY}
**Issue:** #{ISSUE_NUMBER}
**분석 대상:** {MODEL_LIST}

## 모델별 요약

| 항목 | {MODEL_1} | {MODEL_2} | ... |
|------|-----------|-----------|-----|
| 완성도 | {SCORE_1}/5 | {SCORE_2}/5 | ... |
| 프롬프트 횟수 | {COUNT_1}회 | {COUNT_2}회 | ... |
| 인풋 토큰 | {INPUT_1} | {INPUT_2} | ... |
| 아웃풋 토큰 | {OUTPUT_1} | {OUTPUT_2} | ... |
| 소요 시간 | {DURATION_1}분 | {DURATION_2}분 | ... |
| 수동 수정 | {MANUAL_1} | {MANUAL_2} | ... |
| 변경 라인 | +{LINES_1} | +{LINES_2} | ... |
| 지침서 | {GUIDE_1} | {GUIDE_2} | ... |

> 토큰 N/A인 모델은 해당 셀에 N/A 표기, 집계 제외

## AI 종합 분석

모든 세션 데이터와 개별 리포트를 바탕으로 다음을 비교 분석한다:

- **이번 태스크 최적 모델**: L{LEVEL} 레벨 프롬프트에서 가장 우수한 모델과 그 이유.
- **모델별 특징**: 각 모델이 어떤 방식으로 접근했는가. 구현 전략, 코드 구조의 차이.
- **주목할 차이점**: 완성도, 토큰 효율성, 소요 시간, 수동 수정 필요성의 주요 차이.
- **프롬프트 레벨 적응도**: L{LEVEL} 레벨 프롬프트에 대한 모델별 이해도와 대응 방식.
- **지침서 영향**: 지침서를 사용한 모델이 있을 경우, 결과에 미친 영향.
```

### 5단계: 커밋

```bash
mkdir -p reports/{COMBINED_SLUG}
git add reports/{COMBINED_SLUG}/
git commit -m "feat: add vibe-eval report for {COMBINED_SLUG}"
```

### 6단계: 결과 요약 출력

```
✅ 리포트 생성 완료

태스크: {TITLE}
Issue: #{ISSUE_NUMBER}
분석 모델: {MODEL_LIST}

생성된 파일:
  reports/{COMBINED_SLUG}/{MODEL_1_FILENAME}.md
  reports/{COMBINED_SLUG}/{MODEL_2_FILENAME}.md
  reports/{COMBINED_SLUG}/comparison.md  (모델 2개 이상인 경우)
```
