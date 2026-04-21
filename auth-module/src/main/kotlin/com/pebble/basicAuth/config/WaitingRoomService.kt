package com.pebble.basicAuth.config

import org.springframework.stereotype.Service

/**
 * [Phase 4: Virtual Waiting Room Service]
 * (배포 편의를 위해 Redis 없이 항상 ALLOWED를 반환하는 더미 서비스입니다.)
 */
@Service
class WaitingRoomService {

    fun register(userId: String): WaitingStatus {
        return WaitingStatus(status = "ALLOWED", rank = 0)
    }

    fun isUserAllowed(userId: String): Boolean {
        return true
    }

    fun allowUserBatch() {
        // Do nothing
    }

    fun removeFromProceed(userId: String) {
        // Do nothing
    }

    data class WaitingStatus(val status: String, val rank: Long)
}
