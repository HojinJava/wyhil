# card/login-feature [L1] 처리 메모

`cardstackview` 저장소에서는 독립적인 `main()` 함수를 찾지 못했다.

- 라이브러리 모듈은 Android `RecyclerView` 라이브러리로 동작한다.
- 샘플 앱은 `AndroidManifest.xml`과 `MainActivity.kt`를 통해 진입하며, Java/Kotlin `main()` 함수는 없다.

따라서 이 이슈는 코드 주석 추가 대신 진입 구조 확인 결과를 문서로 남긴다.
