package com.finalworksystem.infrastructure.cache

import com.finalworksystem.infrastructure.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GlobalCacheManager(private val sessionManager: SessionManager) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _cacheCleanupEvents = MutableSharedFlow<CacheCleanupEvent>()
    val cacheCleanupEvents: SharedFlow<CacheCleanupEvent> = _cacheCleanupEvents.asSharedFlow()
    
    init {
        scope.launch {
            sessionManager.logoutEvents.collect { logoutEvent ->
                _cacheCleanupEvents.emit(
                    CacheCleanupEvent(
                        userId = logoutEvent.userId,
                        reason = CacheCleanupReason.LOGOUT,
                        timestamp = logoutEvent.timestamp
                    )
                )
            }
        }
    }
    
    suspend fun triggerCacheCleanup(userId: Int?, reason: CacheCleanupReason = CacheCleanupReason.MANUAL) {
        _cacheCleanupEvents.emit(
            CacheCleanupEvent(
                userId = userId,
                reason = reason,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

data class CacheCleanupEvent(
    val userId: Int?,
    val reason: CacheCleanupReason,
    val timestamp: Long
)

enum class CacheCleanupReason {
    LOGOUT,
    USER_SWITCH,
    MANUAL,
    TOKEN_EXPIRED
}
