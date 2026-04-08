# SuperSocket KGC2016 예제 — Codebase 분석

## 프로젝트 개요

이 프로젝트는 KGC(Korea Game Conference) 2016에서 발표된 "오픈소스 네트워크 엔진 SuperSocket 사용하기" 강연의 예제 코드입니다. SuperSocket 라이브러리를 사용하여 바이너리 패킷 기반의 TCP 서버와 클라이언트를 구현하는 방법을 단계별로 시연합니다.

- 발표자료: `{KGC2016]오픈소스 네트워크 엔진 SuperSocket 사용하기.pptx`
- 참고 자료: [SuperSocket_Samples](https://github.com/jacking75/SuperSocket_Samples) (채팅 서버 예제)

---

## 디렉토리 구조

```
supersocket/
├── BinaryPacketClient/               # WinForms 기반 테스트 클라이언트
│   ├── BinaryPacketClient.sln
│   ├── BinaryPacketClient.csproj
│   ├── ClientSocket.cs               # 저수준 TCP 소켓 래퍼
│   ├── MainForm.cs / .Designer.cs    # UI 및 패킷 송수신 로직
│   ├── Program.cs
│   └── Properties/
│
├── BinaryPacketServer/               # 단일 포트 SuperSocket 서버
│   ├── BinaryPacketServer.sln
│   ├── BinaryPacketServer.csproj
│   ├── MainServer.cs                 # AppServer 구현체 (포트 18732)
│   ├── ReceiveFilter.cs              # 바이너리 패킷 파싱 필터
│   ├── PacketHandlers.cs             # 패킷 ID 열거형 및 핸들러
│   ├── DevLog.cs                     # 스레드 안전 로그 유틸리티
│   ├── MainForm.cs / .Designer.cs    # 서버 UI
│   ├── Program.cs
│   ├── packages.config
│   └── Properties/
│
├── BinaryPacketServer_MultiPort/     # 멀티 포트 SuperSocket 서버
│   ├── BinaryPacketServer_MultiPort.sln
│   ├── BinaryPacketServer_MultiPort.csproj
│   ├── MainServer1.cs                # 첫 번째 서버 인스턴스 (포트 32451)
│   ├── MainServer2.cs                # 두 번째 서버 인스턴스 (포트 32452)
│   ├── ReceiveFilter.cs              # 바이너리 패킷 파싱 필터 (동일 구조)
│   ├── PacketHandlers.cs             # 패킷 핸들러
│   ├── DevLog.cs                     # 로그 유틸리티
│   ├── MainForm.cs / .Designer.cs    # 두 서버를 동시에 구동하는 UI
│   ├── Program.cs
│   ├── supersocket.cmd               # Windows 실행 스크립트
│   ├── supersocket.sh                # Unix 실행 스크립트
│   ├── Config/
│   │   ├── log4net.config
│   │   └── log4net.unix.config
│   ├── packages.config
│   └── Properties/
│
└── README.md
```

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| 언어 | C# (.NET Framework 4.5) |
| UI 프레임워크 | Windows Forms (WinForms) |
| 네트워크 엔진 | SuperSocket 1.6.6.1 |
| 로깅 | log4net 2.0.5, 커스텀 DevLog |
| JSON 직렬화 | Newtonsoft.Json 9.0.1 |
| 패킷 구조 | 고정 헤더 12바이트 바이너리 프로토콜 |
| 빌드 시스템 | Visual Studio Solution (.sln / .csproj) |

---

## 각 프로젝트 설명

### BinaryPacketClient

SuperSocket 서버를 테스트하기 위한 WinForms 클라이언트 애플리케이션입니다.

- `System.Net.Sockets.Socket`을 직접 사용하는 저수준 TCP 클라이언트
- UI에서 서버 IP/포트 입력, 접속, 에코 메시지 전송이 가능
- 에코 패킷을 직접 직렬화하여 바이너리 형태로 전송
- `async/await` 패턴으로 블로킹 없이 송수신 처리
- 패킷 구조: `PacketID(4) + Value1(2) + Value2(2) + BodyLen(4) + Body(N)`

### BinaryPacketServer

SuperSocket을 사용한 단일 포트 TCP 에코 서버입니다.

- 포트 18732에서 최대 100개 연결을 수락
- `FixedHeaderReceiveFilter`로 고정 헤더 바이너리 프로토콜 파싱
- 패킷 ID를 키로 하는 `Dictionary<int, Action<...>>` 기반 핸들러 디스패치
- WinForms UI에서 서버 시작/로그 표시
- `REQ_ECHO(1)` 패킷 하나를 지원하며 수신한 Body를 그대로 클라이언트에 반송

### BinaryPacketServer_MultiPort

하나의 애플리케이션 안에서 두 개의 독립적인 SuperSocket 서버 인스턴스를 동시에 실행하는 예제입니다.

- `MainServer1` (포트 32451)과 `MainServer2` (포트 32452)를 각각 별도 `AppServer`로 구동
- 두 서버는 서로 다른 `NetworkSession` 타입(`NetworkSession`, `NetworkSession2`)을 사용
- 실행 로그에 어떤 포트로 접속되었는지 표기하여 멀티 포트 동작을 시각적으로 확인 가능
- Windows/Unix 양쪽 실행 스크립트와 log4net 설정 파일을 포함

---

## 주요 클래스/파일

### ReceiveFilter (공통 구조, 양 서버 프로젝트에 동일하게 존재)

```
헤더 레이아웃 (12바이트 고정):
  offset 0  : PacketID (Int32, 4바이트)
  offset 4  : Value1   (Int16, 2바이트)
  offset 6  : Value2   (Int16, 2바이트)
  offset 8  : BodySize (Int32, 4바이트)
  이후       : Body     (가변 길이)
```

`FixedHeaderReceiveFilter<EFBinaryRequestInfo>`를 상속하며, `GetBodyLengthFromHeader`에서 헤더 8번째 바이트 위치의 Int32를 바디 길이로 추출하고 `ResolveRequestInfo`에서 헤더 필드를 파싱하여 `EFBinaryRequestInfo` 객체를 생성합니다. 빅엔디안 환경을 위한 바이트 역전 처리도 포함되어 있습니다.

### EFBinaryRequestInfo

`BinaryRequestInfo`를 확장한 커스텀 요청 정보 클래스입니다. `PacketID`, `Value1`, `Value2` 세 필드를 헤더에서 파싱하여 보유하며, 핸들러로 전달됩니다.

### MainServer / MainServer1 / MainServer2

`AppServer<TSession, TRequestInfo>`를 상속하는 핵심 서버 클래스입니다.

- 생성자에서 `NewSessionConnected`, `SessionClosed`, `NewRequestReceived` 이벤트를 구독
- `InitConfig()`로 포트, IP, 최대 접속 수 등 서버 설정을 구성
- `CreateServer()`에서 `Setup()` 호출 후 핸들러 등록
- `RegistHandler()`에서 `HandlerMap`에 패킷 ID별 처리 함수를 등록
- `RequestReceived`에서 `HandlerMap`을 조회하여 해당 핸들러 호출

### DevLog

스레드 안전한 로그 큐 유틸리티입니다.

- `ConcurrentQueue<string>` 기반으로 멀티스레드 환경에서 안전하게 로그 메시지를 축적
- `CallerMemberName`, `CallerLineNumber` 특성으로 호출 위치를 자동 기록
- WinForms 타이머(32ms 간격)에서 폴링하여 UI 리스트박스에 표시

### ClientSocket

`System.Net.Sockets.Socket`을 직접 감싼 단순 TCP 클라이언트 래퍼입니다.

- `conn()`: 지정 IP/포트에 동기 방식으로 TCP 연결
- `s_read()`: 수신 버퍼(4096바이트)로 데이터 읽기
- `s_write()`: 바이트 배열을 그대로 전송
- `close()`: 소켓 닫기

---

## 패킷 프로토콜

```
+----------+--------+--------+----------+------------------+
| PacketID | Value1 | Value2 | BodySize | Body             |
|  4바이트  | 2바이트 | 2바이트 |  4바이트  | BodySize 바이트   |
+----------+--------+--------+----------+------------------+
                헤더 12바이트 (고정)       바디 (가변)
```

현재 구현된 패킷 ID:
- `REQ_ECHO = 1`: 클라이언트가 보낸 Body를 그대로 반송

---

## 학습 포인트

1. **SuperSocket AppServer 패턴**: `AppServer<TSession, TRequestInfo>` 상속으로 세션/요청 생명주기 관리
2. **FixedHeaderReceiveFilter**: 고정 헤더 바이너리 프로토콜의 패킷 경계 처리 자동화
3. **핸들러 디스패치 테이블**: `Dictionary<int, Action<...>>`으로 패킷 ID 기반 라우팅
4. **멀티 포트 서버**: 동일 프로세스에서 독립적인 `AppServer` 인스턴스 다수 운용
5. **스레드 안전 로깅**: `ConcurrentQueue`와 UI 타이머 폴링을 조합한 로그 표시 패턴
