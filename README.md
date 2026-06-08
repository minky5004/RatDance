# 🐭 RatDance — GIF 캐릭터와 함께하는 스터디 타이머

공부할 때 화면 한구석에 올려두는 **투명 배경 데스크탑 타이머**입니다.  
춤추는 쥐 GIF를 옆에 두고 **스톱워치 / 카운트다운** 두 가지 모드로 집중 시간을 측정합니다.

## 🎯 주요 기능

- ✅ **스톱워치 모드** — 경과 시간을 실시간으로 측정
- ✅ **카운트다운 모드** — H:M:S 직접 입력 후 카운트다운, 완료 시 알림음 + 화면 깜빡임
- ✅ **GIF 캐릭터 표시** — 애니메이션 GIF를 타이머 옆에 표시
- ✅ **우클릭 GIF 변경** — 캐릭터 우클릭으로 원하는 GIF로 교체 가능
- ✅ **투명 배경** — 윈도우 프레임 없이 GIF와 타이머만 화면에 표시
- ✅ **항상 위** — 다른 앱 위에 항상 표시
- ✅ **자유 이동** — 캐릭터나 타이머를 드래그해 화면 어디든 배치

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java 23 |
| **UI Framework** | JavaFX 21 |
| **스타일링** | JavaFX CSS |
| **설정 저장** | Java Preferences API |
| **Build** | Gradle 9.5.1 |

## 📦 설치 방법

### 사전 요구사항
- Java 23 이상
- Gradle (또는 포함된 `gradlew` 사용)

### 1. 저장소 클론
```bash
git clone https://github.com/minky5004/RatDance.git
cd RatDance
```

### 2. 실행
```bash
# Linux/macOS
./gradlew run

# Windows
gradlew.bat run
```

## 🚀 사용 방법

### 기본 실행
앱을 시작하면 `characters/mouse_dance.gif`가 자동으로 로드됩니다.

### 타이머 조작

| 동작 | 설명 |
|------|------|
| `▶` 버튼 | 타이머 시작 |
| `⏸` 버튼 | 일시정지 |
| `⏹` 버튼 | 초기화 |
| `ESC` 키 | 앱 종료 |
| 캐릭터 / 시간 드래그 | 화면 이동 |
| 캐릭터 우클릭 | GIF 변경 메뉴 표시 |

### 카운트다운 모드
1. **카운트다운** 토글 선택
2. H : M : S 입력
3. `▶` 버튼으로 시작
4. 완료 시 시간이 빨간색으로 깜빡이고 알림음 재생

### GIF 변경
캐릭터 이미지를 **우클릭** → `🎨 GIF 변경` → 원하는 GIF 선택  
선택한 GIF는 자동 저장되어 다음 실행에도 유지됩니다.

### 커스텀 기본 GIF
`characters/mouse_dance.gif`를 원하는 GIF로 교체하면 앱 시작 시 자동으로 로드됩니다.

## 🎓 프로젝트 하이라이트

### 아키텍처 특징
- **단일 책임 구조**: `Main` (진입점) → `StudyTimerApp` (UI + 로직) 분리
- **투명 Stage**: `StageStyle.TRANSPARENT` + CSS 투명 배경으로 배경 없는 UI 구현
- **GIF 경로 탐색**: 작업 디렉토리 → JAR 위치 → Preferences 저장 경로 순으로 폴백
- **JavaFX Timeline**: 1초 간격 `KeyFrame`으로 정확한 시간 측정

### 학습 포인트
- JavaFX 투명 Stage 및 CSS 스타일링
- `ContextMenu`를 활용한 우클릭 메뉴 구현
- `Preferences API`로 설정값 로컬 저장
- `Timeline` / `KeyFrame` / `PauseTransition` 애니메이션 제어

## 📂 프로젝트 구조

```
RatDance/
├── src/
│   └── main/
│       ├── java/com/ratdance/
│       │   ├── Main.java            # JavaFX 진입점
│       │   ├── StudyTimerApp.java   # 메인 UI 및 타이머 로직
│       │   └── TimerMode.java       # STOPWATCH / COUNTDOWN 열거형
│       └── resources/com/ratdance/
│           └── style.css            # 다크 테마 CSS
├── characters/
│   └── mouse_dance.gif              # 기본 캐릭터 GIF
├── build.gradle
└── settings.gradle
```

## 📝 라이선스

MIT License

## 📧 연락처

- Email: minky5004@gmail.com
- GitHub: [@minky5004](https://github.com/minky5004)
