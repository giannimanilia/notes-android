package com.gmaniliapp.notes.ui.note.modify

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.repository.NoteRepository
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_EMAIL
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_EMAIL
import com.gmaniliapp.notes.util.Event
import com.gmaniliapp.notes.util.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ModifyNoteViewModel @ViewModelInject constructor(
    private val repository: NoteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _note = MutableLiveData<Event<Resource<Note>>>()
    val note: LiveData<Event<Resource<Note>>>
        get() = _note

    fun insertNote(note: Note) = GlobalScope.launch {
        repository.insertNote(note)
    }

    fun updateNote(note: Note) = GlobalScope.launch {
        repository.updateNote(note)
    }

    fun selectNoteById(noteId: String) = viewModelScope.launch {
        _note.postValue(Event(Resource.loading(null)))
        val note = repository.selectNoteById(noteId)
        note?.let {
            _note.postValue(Event(Resource.success(it)))
        } ?: _note.postValue(Event(Resource.error(null)))
    }

    fun getLoggedInEmail(): String {
        return sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, DEFAULT_NO_EMAIL)
            ?: DEFAULT_NO_EMAIL
    }
}