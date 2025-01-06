<p align="center">
    <img src="./readme/logo.png" alt="사다줘로고" width="250px">
</p>

## 목차
<li>[프로젝트 소개](#OverView)</li>
<li>[기술 스택](#Stacks)</li>
<li>[시비스 기능](#service)</li>

## OverView
<div align="center">

### 프로젝트 개요
프로젝트명 : SDZ (사다줘) <br>
개발 기간 : 2024.12.09 ~2025.01.06 <br>
개발 멤버 : 김영철 김주희 이강산 정은주 정준민

### 프로젝트 설명
의자를 전문적으로 판매하는 온라인 쇼핑몰 <br>
관리자가 판매자인 B2C 모델

### 배포 주소
>https://elice-sdz.duckdns.org/ <br>

테스트 계정 <br>
ID : test <br>
PW : passw0rd
</div>

## Stacks
<div align="center">

#### Platforms & Languages
<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=Spring&logoColor=white" />
<img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=flat&logo=springsecurity&logoColor=white" />
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white" />
<img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white" />
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white" /> <br>

<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=white" />
<img src="https://img.shields.io/badge/React-61DAFB?style=flat&logo=react&logoColor=white" />
<img src="https://img.shields.io/badge/ChakraUI-319795?style=flat&logo=chakraui&logoColor=white" />
<img src="https://img.shields.io/badge/Zustand-000000?style=flat&logo=Zustand&logoColor=white" /> <br>

#### Tools
<img src="https://img.shields.io/badge/intellij IDE-000000?style=flat&logo=intellijidea&logoColor=white" />
<img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white" />
<img src="https://img.shields.io/badge/GitLab-FC6D26?style=flat&logo=gitlab&logoColor=white" />
<img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white" />
</div>

## Service
- 회원
  - 회원 CRUD
  - 소셜 API (google, kakao, naver)
  - 다음 주소 API
- 상품
  - 상품 CRUD
  - 키워드 Search
- 주문
  - 주문 CRUD
  - 로그인 회원만 주문도메인 접근 가능
- 장바구니
  - 장바구니 CRUD
  - 비회원 로컬스토리지에 장바구니 저장
  - 로그인 시 기존 장바구니에 머지 후 DB 저장
- 카테고리
  - 카테고리 CRUD
  - 관리자 권한 사용자만 접근 가능
  - 자기참조 서브 카테고리 구현
