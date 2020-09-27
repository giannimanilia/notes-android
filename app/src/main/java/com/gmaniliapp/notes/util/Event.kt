package com.gmaniliapp.notes.util

open class Event<out T>(private val content: T) {

    var handled = false
        private set

    fun getContentIfNotHandled() = if (handled) {
        null
    } else {
        handled = true
        content
    }

    fun getContent() = content
}