# Phase 3: OAuth2.0 소셜 로그인 통합 저널

## 1. 진행 상태 (Progress)
- [x] Phase 3-1: OAuth2.0 의존성 및 데이터 모델 확장 (2026-03-31)
- [x] Phase 3-2: IdP(Google, GitHub) 추상화 및 유저 정보 처리 (2026-03-31)
- [x] Phase 3-3: JWT 연동 및 HttpOnly 쿠키 전달 시스템 구축 (2026-03-31)

## 2. 개발 기록 (Journal)

### Phase 3-1: 데이터 모델 확장
- **작업 내용**:
  - `User` 및 `UserEntity`에 `provider`, `providerId` 필드 추가.
  - 소셜 로그인의 경우 비밀번호가 없을 수 있으므로 `password`를 선택사항으로 변경.
  - `UserRepository`에 소셜 식별자로 조회하는 인터페이스 추가.
- **고민 지점**:
  - 동일한 이메일을 가진 다른 제공자의 유저를 통합할지 여부. 현재는 `provider` + `providerId` 조합으로 독립된 유저로 처리하기로 결정함.

### Phase 3-2: OAuth2 서비스 구현
- **작업 내용**:
  - `OAuth2UserInfo` 인터페이스를 통한 다중 IdP 대응 구조 설계.
  - `CustomOAuth2UserService` 구현: 신규 소셜 유저의 경우 자동 가입(Upsert) 로직 적용.
  - `CustomOAuth2User`를 도입하여 Spring Security 내부 객체와 도메인 모델 간의 결합도 관리.

### Phase 3-3: 성공 핸들러 및 JWT 통합
- **작업 내용**:
  - `OAuth2SuccessHandler` 구현: 인증 성공 시 JWT 생성 및 쿠키 발급.
  - `SecurityConfig` 개편: 기존 Stateless 설정을 유지하면서 OAuth2 로그인 필터 통합.
  - `HttpOnly` 및 `Secure` 쿠키를 사용하여 XSS 공격 방어.

## 3. 회고 (Retrospective)
- **성과**: 기존의 JWT 인증 시스템을 거의 수정하지 않고도 소셜 로그인을 매끄럽게 통합함. 인터페이스 기반 설계를 통해 새로운 소셜 제공자 추가가 용이해짐.
- **아쉬운 점**: 현재 로컬 환경 테스트를 위해 `Secure` 쿠키를 적용했으나, HTTPS 환경이 아닌 경우 브라우저가 쿠키를 차단할 수 있음. 테스트 환경에 따라 유동적인 설정이 필요함.
