package com.gmaniliapp.notes.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> ResultType,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    emit(Resource.loading(null))
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))
        try {
            val fetched = fetch()
            query().map { Resource.success(saveFetchResult(fetched)) }
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

inline fun <ResultType, RequestType> networkSyncResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline sync: suspend (ResultType) -> RequestType,
    crossinline processSyncResult: suspend (RequestType) -> Unit,
    crossinline onSyncFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldSync: (ResultType) -> Boolean = { true }
) = flow {
    emit(Resource.loading(null))
    val localData = query().first()

    val flow = if (shouldSync(localData)) {
        emit(Resource.loading(localData))
        try {
            val remoteData = sync(localData)
            processSyncResult(remoteData)
            query().map { Resource.success(it) }
        } catch (t: Throwable) {
            onSyncFailed(t)
            query().map {
                Resource.error(it)
            }
        }
    } else {
        query().map { Resource.success(it) }
    }
    emitAll(flow)
}