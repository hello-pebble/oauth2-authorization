package com.pebble.baseAuth.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * [Phase 4: Filtering Service]
 * 실제 버킷의 생성과 토큰 소모 로직을 담당하는 핵심 엔진입니다.
 * 향후 Redis 연동 시 이 클래스의 내부 구현만 변경하면 됩니다.
 */
@Service
class RateLimitService {

    // IP별 버킷 저장소 (메모리 방식 -> 추후 Redis 기반 ProxyManager로 확장 가능)
    private val buckets = ConcurrentHashMap<String, Bucket>()

    /**
     * 특정 키(IP 등)에 대해 토큰 소모를 시도합니다.
     */
    fun tryConsume(key: String): Boolean {
        val bucket = buckets.computeIfAbsent(key) { createDefaultBucket() }
        return bucket.tryConsume(1)
    }

    /**
     * 기본 버킷 설정: 1초당 5개 충전, 최대 10개 보유 가능
     * (이 설정은 향후 application.yaml에서 관리하도록 확장 가능)
     */
    private fun createDefaultBucket(): Bucket {
        val limit = Bandwidth.classic(10, Refill.intervally(5, Duration.ofSeconds(1)))
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }
}
