package com.gmaniliapp.notes.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    emit(Resource.loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))
        try {
            val fetched = fetch()
            saveFetchResult(fetched)
            query().map { Resource.success(it) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            query().map {
                Resource.error(it)
            }
        }
    } else {
        query().map { Resource.success(it) }
    }
    emitAll(flow)
}