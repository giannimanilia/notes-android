package com.gmaniliapp.notes.ui.note.overview

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmaniliapp.notes.data.remote.BasicAuthInterceptor
import com.gmaniliapp.notes.data.repository.NoteRepository
import com.gmaniliapp.notes.util.Constants
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_EMAIL
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_PASSWORD
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_EMAIL
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_PASSWORD

class NoteOverviewViewModel @ViewModelInject constructor(
    private val repository: NoteRepository,
    private val sharedPreferences: SharedPreferences,
    private val basicAuthInterceptor: BasicAuthInterceptor
) : ViewModel() {

    private val _navigateToLogout = MutableLiveData<Boolean>()
    val navigateToLogout: LiveData<Boolean>
        get() = _navigateToLogout

    private fun displayLogout() {
        _navigateToLogout.value = true
    }

    fun logout() {
        sharedPreferences.edit()
            .putString(KEY_LOGGED_IN_EMAIL, DEFAULT_NO_EMAIL).apply()
        sharedPreferences.edit()
            .putString(KEY_LOGGED_IN_PASSWORD, DEFAULT_NO_PASSWORD)
            .apply()

        displayLogout()
    }

    fun displayLogoutCompleted() {
        _navigateToLogout.value = false
    }
}