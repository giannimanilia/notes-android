package com.gmaniliapp.notes.util

object Constants {

    const val BASE_URL = "http://192.168.1.157:8001"

    const val DATABASE_NAME = "NOTES_APP"

    const val ENCRYPTED_SHARED_PREFERENCES_NAME = "encrypted_shared_preferences"

    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"

    const val KEY_LOGGED_IN_PASSWORD = "KEY_LOGGED_IN_PASSWORD"

    val IGNORE_AUTH_URLS = listOf("/rest/v1/auth/register", "/rest/v1/auth/login")
}