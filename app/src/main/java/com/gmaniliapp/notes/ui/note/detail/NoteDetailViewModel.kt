package com.gmaniliapp.notes.ui.note.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.repository.NoteRepository
import com.gmaniliapp.notes.util.Event
import com.gmaniliapp.notes.util.Resource
import kotlinx.coroutines.launch

class NoteDetailViewModel @ViewModelInject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _note = MutableLiveData<Event<Resource<Note>>>()
    val note: LiveData<Event<Resource<Note>>>
        get() = _note

    private val _addOwnerStatus = MutableLiveData<Event<Resource<String>>>()
    val addOwnerStatus: LiveData<Event<Resource<String>>> = _addOwnerStatus

    fun addOwnerToNote(owner: String, noteID: String) {
        _addOwnerStatus.postValue(Event(Resource.loading(null)))

        if (owner.isEmpty() || noteID.isEmpty()) {
            _addOwnerStatus.postValue(Event(Resource.error("The owner can't be empty")))
            return
        }

        viewModelScope.launch {
            val result = repository.addOwnerToNote(owner, noteID)
            _addOwnerStatus.postValue(Event(result))
        }
    }

    fun selectNoteById(noteId: String) = viewModelScope.launch {
        _note.postValue(Event(Resource.loading(null)))
        val note = repository.selectNoteById(noteId)
        note?.let {
            _note.postValue(Event(Resource.success(it)))
        } ?: _note.postValue(Event(Resource.error(null)))
    }
}