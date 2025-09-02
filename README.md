# TeamUp - 팀 매칭 플랫폼

## 📱 프로젝트 개요

 머신러닝 기반 시너지 분석을 통해 최적의 팀원 매칭을 도와, 공모전 팀 구성 과정에서 팀원 모집의 어려움을 해결하도록 돕는 팀 매칭 플랫폼입니다.

## ✨ 주요 기능

### 🏠 홈
- 최신 공모전 및 팀 모집 정보

### 🏆 공모전
- 다양한 카테고리의 공모전 정보 제공
- 공모전 상세 정보 안내
- 카테고리별 필터링 (웹/앱, AI/데이터 사이언스, 아이디어/기획, IoT/임베디드, 게임, 정보보안/블록체인)

### 👥 팀 모집 게시판
- 팀원 모집 게시글 작성 및 조회
- 지원자 관리 및 팀 구성
- 팀 시너지 점수 시스템

### 👤 마이페이지
- 개인정보 관리
- 개인 프로필 관리
- 사용 가능한 기술 및 원하는 역할
- 성향 테스트
- 공모전 경험 및 수상 이력 관리

## 🛠 기술 스택
<img src="https://img.shields.io/badge/androidstudio-34A853?style=for-the-badge&logo=android&logoColor=white"> <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 

<img src="https://img.shields.io/badge/firebase-DD2C00?style=for-the-badge&logo=firebase&logoColor=white"> 

## 🚀 설치 및 실행

### 필수 요구사항
- Android Studio Arctic Fox 이상
- Android SDK API 26 이상
- Java 11

### 설치 방법

1. **저장소 클론**
   ```bash
   git clone [repository-url]
   cd teamup-android
   ```

2. **Firebase 설정**
   - Firebase Console에서 프로젝트 생성
   - `google-services.json` 파일을 `app/` 디렉토리에 추가

3. **API 서버 설정**
   - `app/src/main/java/kr/mojuk/teamup/api/RetrofitClient.java`에서 서버 URL 설정

4. **프로젝트 빌드**
   ```bash
   ./gradlew build
   ```

5. **앱 실행**
   - Android Studio에서 프로젝트 열기
   - 에뮬레이터 또는 실제 기기에서 실행

## 📱 실행 화면



| 로그인| 회원가입_개인정보 작성| 회원가입_스킬/역할|
| --- | --- | --- |
| ![로그인](https://github.com/user-attachments/assets/59a4cd65-ca0a-4664-b6e6-b946dcb3c574) | ![회원가입1](https://github.com/user-attachments/assets/ba792547-a14d-4b1f-89b9-f047f7c1a31b) | ![회원가입2](https://github.com/user-attachments/assets/1e75ff8e-e499-45a5-ab75-49ee49ffebdf) |

| 회원가입_경험 작성| 회원가입_성향테스트| 메인 홈 화면|
| --- | --- | --- |
| ![회원가입3](https://github.com/user-attachments/assets/edf4422d-2797-461d-a958-4d690d5a54f9) | ![회원가입4](https://github.com/user-attachments/assets/04f7248f-e183-4845-bc8b-9795359a895b) |![메인홈](https://github.com/user-attachments/assets/d4e26f89-f632-4bbc-ab58-6f7021f70626) |

| 마이페이지| 마이페이지_회원정보| 마이페이지_프로필|
| --- | --- | --- |
| ![마이페이지](https://github.com/user-attachments/assets/4830dc76-d232-48d3-865b-9b6ca115e1ef) | ![마이페이지회원정보](https://github.com/user-attachments/assets/b112ab25-ff69-4ccb-8e1b-8e732a05714d)| ![마이페이지프로필](https://github.com/user-attachments/assets/3108da14-940b-48cc-b741-ac6c3297f2e7) |

| 마이페이지_기술역할| 마이페이지_경험| 마이페이지_성향테스트|
| --- | --- | --- |
| ![마이페이지기술역할](https://github.com/user-attachments/assets/b7406368-5e72-4806-9a1b-bb805de34bb0) | ![마이페이지경험](https://github.com/user-attachments/assets/483c85fb-1759-454b-8957-370036239467) | ![마이페이지성향테스트](https://github.com/user-attachments/assets/8d811f24-e224-4b97-8acd-d9ee4da92b96) |

| 내 참여 공모전 | 공모전 목록| 공모전 상세|
| --- | --- | --- |
| ![내참여공모전](https://github.com/user-attachments/assets/5accb7d2-786a-4c04-939c-3d4b331653b5) | ![공모전목록](https://github.com/user-attachments/assets/173257fa-48ce-4833-b0fb-cbc300a8fd9b) | ![공모전상세](https://github.com/user-attachments/assets/a6f130f7-6500-4cea-b1bc-97ab634a222a) |

| 공모전 모집글 작성| 공모전 모집 게시글 목록| 공모전 모집 게시글 상세|
| --- | --- | --- |
| ![모집글작성](https://github.com/user-attachments/assets/3e00251b-2fa3-4d36-8657-3538fcb70964) | ![모집게시글목록](https://github.com/user-attachments/assets/1d717637-01b5-4c89-af68-d36f40b1f371) | ![게시글목록상세](https://github.com/user-attachments/assets/0c4b891d-8eb2-4267-bd2b-c970d24e4d3e) |

| 지원자 리스트| 시너지 분석| 시너지 분석2|
| --- | --- | --- |
| ![지원자리스트](https://github.com/user-attachments/assets/caf69a91-4935-4f13-8103-c5585f9656c8) | ![시너지분석](https://github.com/user-attachments/assets/a6504ea8-9ef0-429d-91cb-2ef19dd6f9f4) | ![시너지분석2](https://github.com/user-attachments/assets/65ec74df-fecf-41a7-9eb1-96402201e9c5) |

| 알람_지원자 추가| 알람_지원 수락| 알람_댓글 추가|
| --- | --- | --- |
| ![지원자추가알람](https://github.com/user-attachments/assets/3df19cfe-05c8-43b1-8950-32e94a3ee426) | ![지원수락알람](https://github.com/user-attachments/assets/e2101947-c00b-4ab2-afbf-3c012908bd29) | ![댓글추가알람](https://github.com/user-attachments/assets/d698a19d-fb46-44f0-824b-61a19c4d8164) |
