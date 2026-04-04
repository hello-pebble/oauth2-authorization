# Phase 4 Troubleshooting: Filtering Issues

## 1. 프록시 환경에서의 클라이언트 IP 누락
*   **증상**: 로드밸런서(AWS ALB, Nginx) 뒤에서 서버가 구동될 때, 모든 요청이 로드밸런서의 IP로 기록되어 모든 사용자가 하나의 버킷을 공유하는 문제.
*   **해결**: `X-Forwarded-For` 헤더를 최우선으로 확인하도록 `RateLimitFilter` 로직 보완.

## 2. Redisson 호환성 문제 (Lettuce와의 충돌)
*   **증상**: 스프링 부트 기본 `lettuce` 라이브러리와 `redisson`이 동시에 설정될 때 발생하는 Bean 충돌.
*   **해결**: `RedissonAutoConfiguration`을 커스텀하거나, 설정 파일에서 Redis 연결 정보 명시적 분리.

## 3. 429 응답 시의 클라이언트 사이드 대응
*   **증상**: 프론트엔드에서 429 에러를 받았을 때 단순히 실패로 처리하여 사용자가 혼란을 겪는 상황.
*   **해결**: 응답 JSON에 `message`를 명확히 포함하고, 프론트엔드에서 "잠시 후 시도" 가이드를 노출하도록 협의.

## 4. 메모리 릭 (Memory Leak)
*   **증상**: IP가 무한히 늘어날 경우 `ConcurrentHashMap`이 가득 차서 JVM OOM(Out of Memory) 발생 가능성.
*   **해결**: 일정 시간 동안 요청이 없는 IP의 버킷을 자동으로 제거하는 `ExpirePolicy` 도입 또는 Redis 기반의 분산 버킷(TTL 지원) 사용으로 전환.
