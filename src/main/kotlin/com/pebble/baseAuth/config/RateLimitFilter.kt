package com.pebble.baseAuth.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * [Phase 4: Filtering Filter - Refactored]
 * 비정상 트래픽을 감지하고 차단하는 '관문' 필터입니다.
 * 비즈니스 로직(Bucket 생성 등)은 RateLimitService에 위임했습니다.
 */
@Component
class RateLimitFilter(
    private val rateLimitService: RateLimitService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val clientIp = extractClientIp(request)
        
        // 1. 서비스에 토큰 소모를 요청하여 트래픽 허용 여부 결정
        if (rateLimitService.tryConsume(clientIp)) {
            // [정상]: 다음 필터로 진행
            chain.doFilter(request, response)
        } else {
            // [비정상]: 429 에러 반환 및 즉시 차단
            sendErrorResponse(response)
        }
    }

    /**
     * 클라이언트 IP 식별 (X-Forwarded-For 헤더 우선 확인)
     */
    private fun extractClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For")?.split(",")?.get(0) 
            ?: request.remoteAddr
    }

    /**
     * 표준화된 에러 응답 전송
     */
    private fun sendErrorResponse(response: HttpServletResponse) {
        response.apply {
            status = HttpStatus.TOO_MANY_REQUESTS.value()
            contentType = "application/json"
            characterEncoding = "UTF-8"
            writer.write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."
                }
            """.trimIndent())
        }
    }
}
