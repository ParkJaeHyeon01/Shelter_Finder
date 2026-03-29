# Shelter_Finder

# 비상 수용시설 찾기 플랫폼 (Shelter Finder)

> **공공 API를 활용하여 사용자의 위치 기반으로 인근 수용시설 정보를 실시간으로 제공하는 안드로이드 애플리케이션입니다.**
> 2025년 6월 모바일 프로그래밍 프로젝트 최종 결과물입니다.

---

## 기술 스택 (Tech Stack)

### **Development Environment**
* **Language & IDE**: Kotlin, Android Studio
* **Architecture**: **MVVM (Model-View-ViewModel)** 패턴 적용

### **Libraries & Frameworks**
* **Local Database**: **Room (Jetpack)** - SQLite 기반 로컬 캐싱 및 데이터 관리
* **Network**: **Retrofit2, GSON** - REST API 통신 및 JSON 파싱
* **UI Components**: RecyclerView, SearchView, Glide (이미지 로딩)
* **Reactive Programming**: **StateFlow** 기반 실시간 데이터 스트림 및 UI 갱신
* **Hybrid**: **WebView + Kakao Maps JavaScript API** 연동

---

## 주요 역할 및 기여 (Frontend & Data Management)

### **1. 데이터 핸들링 및 API Mashup (웹 풀스택 역량 강조)**
* **비정형 공공 데이터 가공**: 재난안전데이터공유플랫폼의 비표준 필드명(한글 약어 등)을 `@SerializedName`을 통해 매핑하고, **Mapper 클래스**를 설계하여 행정구역별로 분절된 데이터를 유의미한 주소 정보로 통합했습니다.
* **멀티 API Mashup**: 수용시설 명칭을 기반으로 **Kakao 이미지 검색 API**를 호출하여 시설의 전경 이미지를 동적으로 매핑하는 로직을 구현했습니다.
* **로컬 DB 캐싱 로직**: 매 실행 시 당일 데이터 갱신 여부를 확인하고, **RoomDB**를 활용해 공공 API 데이터를 로컬에 저장하여 네트워크 호출 비용을 최적화했습니다.

### **2. 프론트엔드 및 하이브리드 인터랙션**
* **Native-Web Bridge 설계**: WebView 내 **Kakao Maps JS API**와 Native 코드를 연결하여, 리스트 아이템 클릭 시 특정 좌표로 지도를 이동시키고 마커를 표시하는 하이브리드 인터랙션을 구현했습니다.
* **위치 기반 필터링 알고리즘**: 사용자의 GPS 좌표를 기준으로 **반경 5km 내**의 수용시설만 추출하고 거리순으로 정렬하는 비즈니스 로직을 처리했습니다.
* **반응형 UI 구현**: **StateFlow**와 **RecyclerView**를 결합하여 데이터 로딩, 검색 결과, 거리 정보 등이 실시간으로 UI에 반영되도록 설계했습니다.

---

## 프로젝트 구조 (Project Structure)

* **`data/local/`**: ShelterEntity, ShelterDao, AppDatabase (Room DB 계층)
* **`data/remote/`**: Retrofit 인터페이스 및 API 응답 DTO 정의
* **`data/mapper/`**: API 데이터(DTO)를 앱 표준 객체(Entity)로 변환 및 가공 로직
* **`presentation/viewmodel/`**: UI 상태 관리 및 데이터 요청 로직 (StateFlow 활용)
* **`presentation/ui/`**: Fragment 기반의 화면 구성 및 WebView 인터랙션 처리
* **`assets/map.html`**: Kakao Maps API 렌더링 및 제어를 위한 HTML/JS 소스

---

## 성과 및 문제 해결
* **데이터 신뢰성 확보**: 불완전한 공공 데이터를 정규화하여 사용자에게 정확한 위치 및 상세 정보(급수, 급식, 지진 대피소 여부 등)를 제공했습니다.
* **UX 최적화**: 스플래시 화면을 통한 리소스 로딩과 데이터 갱신 시스템을 구축하여 서비스의 안정성을 높였습니다.
* **기술적 개선 제안**: 현재의 실시간 갱신 강제 방식을 오프라인 우선(Offline-first) 정책으로 전환하여 네트워크 취약 환경에서도 사용 가능하도록 개선 방안을 수립했습니다.


본 프로젝트는 보안을 위해 API 키를 분리하여 관리합니다. 실행 전 local.properties.example 파일을 참고하여 local.properties에 본인의 API 키를 설정해야 합니다.
