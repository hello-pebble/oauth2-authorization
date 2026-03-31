## Phase 3: OAuth2.0 소셜 로그인 통합

OAuth2.0 표준 프로토콜을 활용하여 외부 인증 서비스를 통합하고, 기존 JWT 시스템과 결합하여 보안성과 편의성을 동시에 확보합니다.

#### 3-1. OAuth2.0 기본 인프라 구축

| 항목              | 세부 내용                                               | 상태 |
|-------------------|---------------------------------------------------------|-----|
| 의존성 추가       | `spring-boot-starter-oauth2-client` 도입                | ✅ |
| 데이터 모델 확장  | `provider`, `providerId` 필드 추가 및 비밀번호 null 허용 | ✅ |
| IdP 추상화 설계   | `OAuth2UserInfo` 인터페이스 및 IdP별 구현체 작성         | ✅ |

---

#### 3-2. OAuth2.0 핵심 서비스 구현

| 항목              | 세부 내용                                               | 상태 |
|-------------------|---------------------------------------------------------|-----|
| OAuth2 유저 서비스| `CustomOAuth2UserService` — 소셜 정보 DB 동기화(Upsert)  | ✅ |
| 커스텀 Principal  | `CustomOAuth2User` — 시스템 도메인 모델과 Security 통합 | ✅ |
| 성공 핸들러       | `OAuth2SuccessHandler` — JWT 발급 및 쿠키 저장           | ✅ |

---

#### 3-3. 보안 및 설정 통합

| 항목              | 세부 내용                                               | 상태 |
|-------------------|---------------------------------------------------------|-----|
| Security 설정     | `SecurityConfig`에 `.oauth2Login()` 흐름 통합           | ✅ |
| JWT 쿠키 전달     | `HttpOnly`, `Secure` 쿠키를 통한 토큰 전달 체계 구축    | ✅ |
| 환경 설정         | `application.yaml`에 Google/GitHub 클라이언트 설정 추가  | ✅ |

---

### 🔗 연관 자료
- **[아키텍처](../architecture/phase3_architecture.md)**
- **[TDR](tdr/phase3-tdr.md)**
- **[트러블슈팅](troubleshooting/phase3-troubleshooting.md)**
- **[구현 저널](PHASE3_JOURNAL.md)**
