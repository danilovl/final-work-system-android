package com.finalworksystem.domain.common.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

inline fun <T> safeFlow(crossinline block: suspend FlowCollector<Result<T>>.() -> Unit): Flow<Result<T>> = flow {
    try {
        block()
    } catch (e: Exception) {
        emit(Result.failure(e))
    }
}.flowOn(Dispatchers.IO)

inline fun <T> safeFlowResult(crossinline block: suspend () -> Result<T>): Flow<Result<T>> = flow {
    try {
        val result = block()
        emit(result)
    } catch (e: Exception) {
        emit(Result.failure(e))
    }
}.flowOn(Dispatchers.IO)
