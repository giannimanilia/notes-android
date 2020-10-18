package com.gmaniliapp.notes.data.remote.response

import io.ktor.http.HttpStatusCode

data class StandardResponse(
    val code: HttpStatusCode,
    val message: Any
) {
    fun isOk(): Boolean {
        return code == HttpStatusCode.OK || code == HttpStatusCode.Created || code == HttpStatusCode.Accepted
    }
}

