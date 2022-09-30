package com.mcs.emkn.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.Router
import com.mcs.emkn.databinding.FragmentSignInBinding
import com.mcs.emkn.ui.auth.viewmodels.SignInInteractor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router

    lateinit var signInInteractor: SignInInteractor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forgotPasswordButton.setOnClickListener {
            router.goToForgotPasswordScreen()
        }
        binding.submitButton.setOnClickListener {
            val login = binding.loginEditText.text?.toString() ?: return@setOnClickListener
            val password = binding.passwordEditText.text?.toString() ?: return@setOnClickListener
            signInInteractor.onSignInClick(login, password)
        }
        subscribeToFormFields()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackButtonPressed()
            this.isEnabled = true
        }
    }

    private fun decideSignInButtonEnabledState(login: String?, password: String?) {
        binding.submitButton.isEnabled = !(login.isNullOrBlank() || password.isNullOrBlank())
    }

    private fun subscribeToFormFields() {
        decideSignInButtonEnabledState(
            login = binding.loginEditText.text?.toString(),
            password = binding.passwordEditText.text?.toString()
        )
        binding.loginEditText.doAfterTextChanged { login ->
            decideSignInButtonEnabledState(
                login = login?.toString(),
                password = binding.passwordEditText.text?.toString()
            )
        }
        binding.passwordEditText.doAfterTextChanged { password ->
            decideSignInButtonEnabledState(
                login = binding.loginEditText.text?.toString(),
                password = password?.toString()
            )
        }
    }

    private fun onBackButtonPressed() {
        val login = binding.loginEditText.text?.toString()
        val password = binding.passwordEditText.text?.toString()
        if (login.isNullOrBlank() && password.isNullOrBlank()) {
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