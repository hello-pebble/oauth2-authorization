# Phase 3: OAuth2.0 소셜 로그인 통합 아키텍처

이 문서는 기존 JWT 기반 Stateless 인증 시스템에 OAuth2.0 소셜 로그인(Google, GitHub 등)을 통합한 아키텍처를 설명합니다.

## 1. 개요
- **목표**: 외부 ID 제공자(IdP)를 통한 간편 로그인 지원 및 JWT 기반 통합 인증 체계 구축.
- **핵심 전략**:
  - `spring-boot-starter-oauth2-client`를 활용한 표준 프로토콜 준수.
  - 소셜 로그인 성공 시 기존 JWT 시스템과 연동하여 Access/Refresh 토큰 발급.
  - 보안 강화를 위해 발급된 토큰은 `HttpOnly` 쿠키를 통해 클라이언트에 전달.

## 2. 구성 요소 (Components)

### 2.1. OAuth2 Client Layer
- **`CustomOAuth2UserService`**: 소셜 서비스로부터 유저 정보를 가져와 우리 시스템의 유저 모델로 변환 및 DB 동기화(Upsert).
- **`OAuth2UserInfo` & 구현체**: 각 IdP(Google, GitHub 등)마다 다른 응답 형식을 통일된 인터페이스로 추상화.
- **`OAuth2UserInfoFactory`**: `registrationId`에 따라 적절한 `OAuth2UserInfo` 객체를 생성하는 팩토리.

### 2.2. Security Layer
- **`SecurityConfig`**: `.oauth2Login()` 설정을 통해 OAuth2 흐름을 필터 체인에 통합.
- **`OAuth2SuccessHandler`**: 인증 성공 후 JWT 발급 및 쿠키 저장 로직을 담당.
- **`CustomOAuth2User`**: Spring Security의 `OAuth2User`를 확장하여 우리 도메인의 `User` 객체를 포함.

### 2.3. Persistence Layer
- **`UserEntity`**: 소셜 정보를 저장하기 위한 `provider`, `providerId` 필드 추가 및 `password` 선택사항화.
- **`UserRepository`**: `provider`와 `providerId` 조합으로 유저를 조회하는 기능 추가.

## 3. 인증 흐름 (Authentication Flow)

1.  **Login Request**: 사용자가 소셜 로그인 버튼 클릭 (`/oauth2/authorization/{provider}`).
2.  **Redirect to IdP**: Spring Security가 사용자를 Google/GitHub 로그인 페이지로 리다이렉트.
3.  **Authorization Code**: 로그인이 완료되면 IdP가 서비스로 Authorization Code를 보냄.
4.  **Token Exchange**: Spring Security가 Code를 Access Token으로 교환.
5.  **User Info Retrieval**: `CustomOAuth2UserService`가 IdP로부터 사용자 정보를 가져옴.
6.  **User Sync**: 가져온 정보를 바탕으로 DB에 유저가 없으면 가입, 있으면 정보를 업데이트.
7.  **Success Handling**: `OAuth2SuccessHandler`가 실행되어 우리 서비스용 JWT(Access/Refresh) 생성.
8.  **Cookie Response**: 생성된 JWT를 `HttpOnly` 쿠키에 담아 클라이언트로 리다이렉트.

## 4. 보안 고려 사항
- **HttpOnly Cookies**: XSS 공격으로부터 토큰을 보호하기 위해 쿠키 방식 채택.
- **CSRF Protection**: REST API 환경이므로 기본적으로 비활성화되어 있으나, 쿠키 방식을 사용하므로 추후 필요 시 `SameSite` 설정 등을 통해 보완 필요.
- **Token Rotation**: 소셜 로그인 시에도 기존의 Refresh Token Rotation 전략을 동일하게 적용.
