package com.finalworksystem.domain.common.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Wraps upstream errors into [Result.failure].
 *
 * The `catch` operator only handles upstream exceptions (exception transparency),
 * and coroutine cancellation is always rethrown instead of being turned into a failure.
 */
inline fun <T> safeFlow(crossinline block: suspend FlowCollector<Result<T>>.() -> Unit): Flow<Result<T>> = flow {
    block()
}.catch { e ->
    if (e is CancellationException || e !is Exception) throw e
    emit(Result.failure(e))
}.flowOn(Dispatchers.IO)

inline fun <T> safeFlowResult(crossinline block: suspend () -> Result<T>): Flow<Result<T>> = flow {
    emit(block())
}.catch { e ->
    if (e is CancellationException || e !is Exception) throw e
    emit(Result.failure(e))
}.flowOn(Dispatchers.IO)
