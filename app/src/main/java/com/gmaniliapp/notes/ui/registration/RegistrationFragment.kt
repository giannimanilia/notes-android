package com.gmaniliapp.notes.ui.registration

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.ui.login.LoginFragmentDirections
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration.*

@AndroidEntryPoint
class RegistrationFragment : BaseFragment(R.layout.fragment_registration) {

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        btnRegister.setOnClickListener {
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()
            val repeatedPassword = etRegisterPasswordConfirm.text.toString()
            viewModel.register(email, password, repeatedPassword)
        }
    }

    private fun subscribeToObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "User successfully registered")
                        onRegistrationCompleted()
                    }
                    Status.ERROR -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Error registering user")
                    }
                    Status.LOADING -> {
                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun onRegistrationCompleted() {
        val navigationOptions = NavOptions.Builder()
            .setPopUpTo(R.id.registrationFragment, true)
            .build()

        findNavController().navigate(
            RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment(),
            navigationOptions
        )
    }
}