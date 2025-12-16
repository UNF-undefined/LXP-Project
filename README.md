# 🏫 Project LXP (가제: UNF)
> **Udemy, Inflearn을 벤치마킹한 학습 경험 플랫폼(LXP) 구축 프로젝트** > 개발 기간: 2025.10.31 ~ 2025.11.14 (약 2주)

<br>

## 1. 👥 Team Members (팀원 소개)
백엔드 5명으로 구성된 팀 입니다. 
도메인 주도 설계(DDD)를 기반으로 각자 독립적인 도메인을 맡아 개발했습니다.

|   이름    |     포지션     | 담당 도메인(Role)                       |                  GitHub                  |
|:-------:|:-----------:|:-----------------------------------|:----------------------------------------:|
| **김성균** | **Backend** | **User (회원가입, 인증/인가, 보안)**         | [Link](https://github.com/UNF-undefined/LXP-Project/tree/develop/src/main/java/com/example/projectlxp/user) |
|   정님    |   Backend   | Course (코스 등록 및커리큘럼 관리)            |                [Link](링크)                |
|   안님    |   Backend   | Category, Content (카테고리 및 수강신청 관리) |                [Link](링크)                |
|   김님    |   Backend   | Review (리뷰평점,필터링 관리)               |                [Link](링크)                |
|   정님    |   Backend   | lecture (강의 및 강좌 등록)               |                [Link](링크)                |

<br>

## 2. 🛠️ Tech Stack (기술 스택)
### Backend
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/jpa-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> 
<br>

### Tools & DevOps
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

<br>

## 3. 👨‍💻 My Contribution (담당 구현 내용)
**담당자: 김성균 (User Domain)**
프로젝트의 기반이 되는 회원 도메인을 맡아 **인증/인가 보안**과 **API 성능 최적화**에 집중했습니다.

### 🔑 핵심 구현 기능
* **JWT 기반 인증/인가 시스템 구축**: Spring Security + JWT FilterChain 구현
* **로그인 성능 개선**: 토큰과 유저 정보를 한 번에 반환하여 API 호출 횟수 50% 단축 (RTT 감소)
* **로그아웃 보안 강화**: 서버 측 토큰 검증 로직 추가 (Stateless의 한계 보완)
* **데이터 무결성 확보**: JPA Auditing(BaseEntity) 적용 및 Soft Delete 구현

### 🔥 트러블 슈팅 (Blog Link)
* [CustomUserDetails 도입](https://velog.io/@doil3588/%ED%9A%8C%EA%B3%A0View-%EC%97%86%EC%9D%B4-%EB%96%A0%EB%82%98%EB%8A%94-%EB%B0%B1%EC%97%94%EB%93%9C-%ED%95%AD%ED%95%B4-LXP-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-User-%EB%8F%84%EB%A9%94%EC%9D%B8)
* [순환참조 해결기](https://velog.io/@doil3588/%ED%9A%8C%EA%B3%A0View-%EC%97%86%EC%9D%B4-%EB%96%A0%EB%82%98%EB%8A%94-%EB%B0%B1%EC%97%94%EB%93%9C-%ED%95%AD%ED%95%B4-LXP-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-User-%EB%8F%84%EB%A9%94%EC%9D%B8)

<br>

## 4. ⚙️ How to Run (실행 방법)
이 프로젝트는 Java 17 및 MySQL 환경에서 실행됩니다.

**1. 레포지토리 클론**
```bash
git clone https://github.com/UNF-undefined/LXP-Project.git
```
