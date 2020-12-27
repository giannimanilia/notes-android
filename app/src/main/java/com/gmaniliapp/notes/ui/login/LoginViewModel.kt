package com.gmaniliapp.notes.ui.login

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmaniliapp.notes.data.remote.BasicAuthInterceptor
import com.gmaniliapp.notes.data.repository.AuthRepository
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_EMAIL
import com.gmaniliapp.notes.util.Constants.DEFAULT_NO_PASSWORD
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_EMAIL
import com.gmaniliapp.notes.util.Constants.KEY_LOGGED_IN_PASSWORD
import com.gmaniliapp.notes.util.Resource
import com.gmaniliapp.notes.util.Status
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val repository: AuthRepository,
    private val sharedPreferences: SharedPreferences,
    private val basicAuthInterceptor: BasicAuthInterceptor
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

    private var currentEmail: String? = DEFAULT_NO_EMAIL
    private var currentPassword: String? = DEFAULT_NO_PASSWORD

    fun login(email: String, password: String) {
        _loginStatus.postValue(Resource.loading(null))

        if (email.isBlank() || password.isBlank()) {
            _loginStatus.postValue(Resource.error("Please fill out all the fields"))
        } else {
            viewModelScope.launch {
                val result = repository.login(email, password)

                _loginStatus.postValue(result)

                result.let {
                    when (result.status) {
                        Status.SUCCESS -> {
                            currentEmail = email
                            currentPassword = password

                            authenticateApi()

                            sharedPreferences.edit()
                                .putString(KEY_LOGGED_IN_EMAIL, currentEmail).apply()
                            sharedPreferences.edit()
                                .putString(KEY_LOGGED_IN_PASSWORD, currentPassword)
                                .apply()
                        }
                        Status.ERROR -> {
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            }
        }
    }

    fun authenticateApi() {
        basicAuthInterceptor.email = currentEmail
        basicAuthInterceptor.password = currentPassword
    }

    fun isLoggedIn(): Boolean {
        currentEmail =
            sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, DEFAULT_NO_EMAIL) ?: DEFAULT_NO_EMAIL
        currentPassword = sharedPreferences.getString(KEY_LOGGED_IN_PASSWORD, DEFAULT_NO_PASSWORD)
            ?: DEFAULT_NO_PASSWORD
        return currentEmail != DEFAULT_NO_EMAIL && currentPassword != DEFAULT_NO_PASSWORD
    }
}