package com.finalworksystem.domain.system_event.repository

import kotlinx.coroutines.flow.Flow

interface PutSystemEventRepository {
    fun changeViewed(eventId: Int): Flow<Result<Boolean>>
    fun changeAllViewed(): Flow<Result<Boolean>>
}