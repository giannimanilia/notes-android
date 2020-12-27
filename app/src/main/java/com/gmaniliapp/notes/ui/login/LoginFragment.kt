package com.gmaniliapp.notes.ui.login

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.isLoggedIn()) {
            onLoginRedirect()
            viewModel.authenticateApi()
        }

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()
            viewModel.login(email, password)
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            )
        }
    }

    private fun onLoginRedirect() {
        val navigationOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()

        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToNoteOverviewFragment(),
            navigationOptions
        )
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        loginProgressBar.visibility = View.GONE
                        showSnackBar("User successfully logged in")
                        onLoginRedirect()
                    }
                    Status.ERROR -> {
                        loginProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Error logging in user")
                    }
                    Status.LOADING -> {
                        loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}