package com.gmaniliapp.notes.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.notes.data.repository.NoteRepository
import com.gmaniliapp.notes.util.Resource
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus: LiveData<Resource<String>> = _registerStatus

    fun register(email: String, password: String, repeatedPassword: String) {
        _registerStatus.postValue(Resource.loading(null))

        if (email.isBlank() || password.isBlank() || repeatedPassword.isBlank()) {
            _registerStatus.postValue(Resource.error("Please fill out all the fields"))
        } else if (password != repeatedPassword) {
            _registerStatus.postValue(Resource.error("Passwords don't match"))
        } else {
            viewModelScope.launch {
                _registerStatus.postValue(repository.register(email, password))
            }
        }
    }
}