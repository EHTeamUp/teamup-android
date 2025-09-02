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


