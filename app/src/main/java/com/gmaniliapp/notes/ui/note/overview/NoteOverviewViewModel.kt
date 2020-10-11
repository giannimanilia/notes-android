package com.gmaniliapp.notes.ui.note.overview

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.remote.BasicAuthInterceptor
import com.gmaniliapp.notes.data.repository.NoteRepository
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_EMAIL
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_PASSWORD
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_EMAIL
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_PASSWORD
import com.gmaniliapp.notes.util.Event
import com.gmaniliapp.notes.util.Resource
import kotlinx.coroutines.launch

class NoteOverviewViewModel @ViewModelInject constructor(
    private val repository: NoteRepository,
    private val sharedPreferences: SharedPreferences,
    private val basicAuthInterceptor: BasicAuthInterceptor
) : ViewModel() {

    private val _navigateToLogout = MutableLiveData<Boolean>()
    val navigateToLogout: LiveData<Boolean>
        get() = _navigateToLogout

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _allNotes = _forceUpdate.switchMap {
        repository.getAllNotes().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

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

    fun syncNotes() = _forceUpdate.postValue(true)

    fun deleteNoteById(noteId: String) = viewModelScope.launch {
        repository.deleteNoteById(noteId)
    }
}