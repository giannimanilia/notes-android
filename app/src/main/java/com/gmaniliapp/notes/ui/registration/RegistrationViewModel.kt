package com.gmaniliapp.notes.ui.registration

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.notes.data.repository.AuthRepository
import com.gmaniliapp.notes.util.Resource
import kotlinx.coroutines.launch

class RegistrationViewModel @ViewModelInject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus: LiveData<Resource<String>> = _registerStatus

    fun register(email: String, password: String, repeatedPassword: String) {
        _registerStatus.postValue(Resource.loading(null))

        if (email.isBlank() || password.isBlank() || repeatedPassword.isBlank()) {
            _registerStatus.postValue(Resource.error("Please fill out all the fields"))
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerStatus.postValue(Resource.error("Please insert a valid email"))
        } else if (password != repeatedPassword) {
            _registerStatus.postValue(Resource.error("Passwords don't match"))
        } else {
            viewModelScope.launch {
                _registerStatus.postValue(repository.register(email, password))
            }
        }
    }
}