package com.mcs.emkn.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcs.emkn.R
import com.mcs.emkn.core.RouterImpl
import com.mcs.emkn.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding

    @Inject
    lateinit var router: RouterImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forgotPasswordButton.setOnClickListener {
            router.goToForgotPasswordScreen()
        }
        subscribeToFormFields()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackButtonPressed()
            this.isEnabled = true
        }
    }

    private fun decideSignInButtonEnabledState(email: String?, password: String?) {
        binding.submitButton.isEnabled = !(email.isNullOrBlank() || password.isNullOrBlank())
    }
    private fun subscribeToFormFields() {
        decideSignInButtonEnabledState(
            email = binding.emailEditText.text?.toString(),
            password = binding.passwordEditText.text?.toString()
        )
        binding.emailEditText.doAfterTextChanged { email ->
            decideSignInButtonEnabledState(
                email = email?.toString(),
                password = binding.passwordEditText.text?.toString()
            )
        }
        binding.passwordEditText.doAfterTextChanged { password ->
            decideSignInButtonEnabledState(
                email = binding.emailEditText.text?.toString(),
                password = password?.toString()
            )
        }
    }

    private fun onBackButtonPressed() {
        val email = binding.emailEditText.text?.toString()
        val password = binding.passwordEditText.text?.toString()
        if (email.isNullOrBlank() && password.isNullOrBlank()) {
            router.back()
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.sign_in_back_alert_dialog_text)
            .setNegativeButton(R.string.sign_in_back_alert_dialog_cancel_button_text) { dialog, _ ->
                dialog?.dismiss()
            }
            .setPositiveButton(R.string.sign_in_back_alert_dialog_ok_button_text) { _, _ ->
                router.back()
            }
            .show()
    }
}