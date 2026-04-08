# CardStackView — Codebase 분석

## 프로젝트 개요

CardStackView는 Tinder 스타일의 스와이프 가능한 카드 뷰 Android 라이브러리이다. Android RecyclerView를 기반으로 구축되어 있으며, 좌/우/상/하 방향으로 카드를 스와이프하거나 되감기(rewind)하는 기능을 제공한다. 버전 2.3.4, Apache 2.0 라이선스로 배포된다.

- GitHub: https://github.com/yuyakaido/CardStackView
- 작성자: yuyakaido
- 라이브러리 artifact: `com.yuyakaido.android:card-stack-view:2.3.4`
- 최소 SDK: API 14 / 타깃 SDK: API 28

---

## 디렉토리 구조

```
cardstackview/
├── build.gradle                          # 루트 빌드 설정 (Kotlin 1.3.50, AGP 3.5.1)
├── settings.gradle                       # 모듈 등록 (cardstackview, sample)
├── gradle.properties
├── gradlew / gradlew.bat
├── gradle/wrapper/
│
├── cardstackview/                        # 라이브러리 모듈
│   ├── build.gradle                      # Android library, bintray-release 플러그인
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/yuyakaido/android/cardstackview/
│       │   ├── CardStackView.java        # 공개 진입점 (RecyclerView 확장)
│       │   ├── CardStackLayoutManager.java  # 핵심 레이아웃 로직
│       │   ├── CardStackListener.java    # 이벤트 콜백 인터페이스
│       │   ├── Direction.java            # Left / Right / Top / Bottom 열거형
│       │   ├── StackFrom.java            # 카드 스택 방향 열거형
│       │   ├── SwipeableMethod.java      # 스와이프 방법 열거형
│       │   ├── Duration.java             # 애니메이션 지속 시간 열거형
│       │   ├── SwipeAnimationSetting.java   # 스와이프 애니메이션 설정 (Builder 패턴)
│       │   ├── RewindAnimationSetting.java  # 되감기 애니메이션 설정 (Builder 패턴)
│       │   └── internal/
│       │       ├── AnimationSetting.java    # 애니메이션 설정 공통 인터페이스
│       │       ├── CardStackDataObserver.java  # 어댑터 데이터 변경 관찰자
│       │       ├── CardStackSetting.java    # 설정값 보관 객체
│       │       ├── CardStackSmoothScroller.java  # SmoothScroller 구현
│       │       ├── CardStackSnapHelper.java  # SnapHelper 구현
│       │       ├── CardStackState.java      # 런타임 상태 관리
│       │       └── DisplayUtil.java         # dp/px 변환 유틸
│       └── res/layout/
│           └── overlay.xml               # 오버레이 뷰 레이아웃
│
└── sample/                               # 데모 앱 모듈
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/yuyakaido/android/cardstackview/sample/
        │   ├── MainActivity.kt           # CardStackListener 구현 예시
        │   ├── CardStackAdapter.kt       # RecyclerView.Adapter 구현
        │   ├── Spot.kt                   # 데이터 모델 (data class)
        │   └── SpotDiffCallback.kt       # DiffUtil.Callback 구현
        └── res/
            ├── layout/activity_main.xml
            ├── layout/item_spot.xml
            ├── drawable/ (오버레이, 아이콘 등)
            └── menu/navigation_main_activity.xml
```

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| 플랫폼 | Android |
| 언어 | Java (라이브러리 핵심), Kotlin (샘플 앱) |
| 빌드 시스템 | Gradle (Android Gradle Plugin 3.5.1) |
| Kotlin 버전 | 1.3.50 |
| AndroidX | RecyclerView 1.0.0 |
| CI | CircleCI |
| 배포 | Bintray (novoda bintray-release 플러그인) |
| 최소 SDK | API 14 |
| 타깃 SDK | API 28 |

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| Manual Swipe | 터치 드래그로 카드 스와이프 |
| Automatic Swipe | `CardStackView.swipe()` 호출로 프로그래매틱 스와이프 |
| Rewind | `CardStackView.rewind()`로 이전 카드 복귀 |
| Cancel | 임계값 미만 드래그 시 자동 취소 |
| Overlay View | 방향에 따라 left/right/top/bottom_overlay 뷰 알파 처리 |
| Overlay Interpolator | 오버레이 알파 변화율 커스터마이징 |
| Paging | DiffUtil 또는 `notifyItemRangeInserted`로 무한 스크롤 구현 |
| Stack From | None / Top / Bottom / Left / Right 등 카드 스택 출발 방향 설정 |
| Visible Count | 화면에 표시할 카드 장수 설정 (기본 3) |
| Translation Interval | 카드 간 오프셋 간격 (dp 단위, 기본 8dp) |
| Scale Interval | 뒤 카드 스케일 비율 (기본 0.95) |
| Swipe Threshold | 스와이프 완료 판정 임계값 (기본 30%) |
| Max Degree | 카드 최대 회전 각도 (기본 20°) |
| Swipe Direction | Horizontal / Vertical / Freedom 방향 제한 |
| Swipe Restriction | 수평/수직 스와이프 개별 허용/차단 |
| Swipeable Method | AutomaticAndManual / Automatic / Manual / None |

---

## 주요 클래스/파일

### CardStackView.java
`RecyclerView`를 확장한 공개 진입점 클래스. `CardStackLayoutManager`만 허용하도록 `setLayoutManager`를 오버라이드하며, `CardStackSnapHelper`를 초기화한다. 어댑터 데이터 변경을 `CardStackDataObserver`로 관찰한다.

- `swipe()`: 현재 상단 카드를 다음 위치로 smoothScroll
- `rewind()`: 현재 상단 카드를 이전 위치로 smoothScroll
- `onInterceptTouchEvent()`: 터치 시작 시 비율 정보 업데이트

### CardStackLayoutManager.java
핵심 레이아웃 엔진. `RecyclerView.LayoutManager`와 `SmoothScroller.ScrollVectorProvider`를 구현한다. 수평/수직 스크롤 처리, 뷰 배치, 번역/스케일/회전/오버레이 업데이트를 담당한다.

- 상태 머신 기반으로 Idle / Dragging / AnimatingSwipe / AnimatedSwipe / RewindAnimating 상태 전환
- 스와이프 완료 감지 후 `Handler.post()`를 통해 `CardStackListener` 콜백 비동기 호출 (RecyclerView 레이아웃 중 콜백 호출로 인한 `IllegalStateException` 방지)

### CardStackListener.java
이벤트 콜백 인터페이스. `DEFAULT` 정적 인스턴스(no-op 구현)를 제공하며, 사용자가 원하는 메서드만 구현할 수 있다.

```java
void onCardDragging(Direction direction, float ratio);   // 드래그 중
void onCardSwiped(Direction direction);                  // 스와이프 완료
void onCardRewound();                                    // 되감기 완료
void onCardCanceled();                                   // 임계값 미만 취소
void onCardAppeared(View view, int position);            // 카드 등장
void onCardDisappeared(View view, int position);         // 카드 퇴장
```

### CardStackSetting.java
레이아웃 매니저가 사용하는 설정값 보관 객체. 모든 필드는 public으로 직접 접근하며, 기본값이 설정되어 있다.

### CardStackState.java
런타임 상태를 관리하는 객체. `Status` 열거형(상태 머신), dx/dy 오프셋, topPosition, targetPosition, proportion을 보관한다. `isSwipeCompleted()`로 스와이프 완료 여부를 판단하고, `getDirection()`으로 현재 이동 방향을 계산한다.

### CardStackSmoothScroller.java
`RecyclerView.SmoothScroller`를 확장해 네 가지 스크롤 유형(AutomaticSwipe, AutomaticRewind, ManualSwipe, ManualCancel)에 따라 애니메이션 동작을 제어한다.

### SwipeAnimationSetting.java / RewindAnimationSetting.java
Builder 패턴으로 애니메이션 방향, 지속 시간, Interpolator를 설정한다.

### sample/MainActivity.kt
`CardStackListener` 구현 예시. DiffUtil을 활용한 페이지네이션, 카드 추가/제거/교체/스왑 등 다양한 데이터 조작 패턴을 시연한다.

---

## 아키텍처 요약

```
CardStackView (RecyclerView)
  ├── CardStackLayoutManager (LayoutManager)
  │     ├── CardStackSetting      # 설정
  │     ├── CardStackState        # 런타임 상태
  │     └── CardStackSmoothScroller  # 애니메이션 스크롤
  ├── CardStackSnapHelper (SnapHelper)
  └── CardStackDataObserver (AdapterDataObserver)
        └── CardStackListener     # 외부 이벤트 콜백
```

라이브러리는 RecyclerView의 `LayoutManager`, `SnapHelper`, `AdapterDataObserver`를 커스텀 구현해 카드 스택 UI를 구현한다. 별도의 외부 의존성 없이 `androidx.recyclerview:recyclerview`만을 사용한다.
