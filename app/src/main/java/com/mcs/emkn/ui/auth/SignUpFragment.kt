package com.mcs.emkn.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.Router
import com.mcs.emkn.databinding.FragmentSignUpBinding
import com.mcs.emkn.ui.auth.viewmodels.SignUpInteractor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router

    lateinit var signUpInteractor: SignUpInteractor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpButton.setOnClickListener {
            signUpInteractor.onSignUpClick(
                email = binding.emailEditText.text?.toString() ?: return@setOnClickListener,
                login = binding.loginEditText.text?.toString() ?: return@setOnClickListener,
                password = binding.passwordEditText.text?.toString() ?: return@setOnClickListener,
                name = binding.firstnameEditText.text?.toString() ?: return@setOnClickListener,
                surname = binding.lastnameEditText.text?.toString() ?: return@setOnClickListener,
            )
        }
        subscribeToFormFields()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackButtonPressed()
            this.isEnabled = true
        }
    }

    private fun decideSignUpButtonEnabledState(
        firstName: String?,
        lastName: String?,
        login: String?,
        email: String?,
        password: String?
    ) {
        binding.signUpButton.isEnabled =
            !(firstName.isNullOrBlank() || lastName.isNullOrBlank() || login.isNullOrBlank() ||
                email.isNullOrBlank() || password.isNullOrBlank())
    }

    private fun subscribeToFormFields() {
        decideSignUpButtonEnabledState(
            firstName = binding.firstnameEditText.text?.toString(),
            lastName = binding.lastnameEditText.text?.toString(),
            login = binding.loginEditText.text?.toString(),
            email = binding.emailEditText.text?.toString(),
            password = binding.passwordEditText.text?.toString()
        )
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                decideSignUpButtonEnabledState(
                    firstName = binding.firstnameEditText.text?.toString(),
                    lastName = binding.lastnameEditText.text?.toString(),
                    login = binding.loginEditText.text?.toString(),
                    email = binding.emailEditText.text?.toString(),
                    password = binding.passwordEditText.text?.toString(),
                )
            }
        }

        binding.firstnameEditText.addTextChangedListener(watcher)
        binding.lastnameEditText.addTextChangedListener(watcher)
        binding.loginEditText.addTextChangedListener(watcher)
        binding.emailEditText.addTextChangedListener(watcher)
        binding.passwordEditText.addTextChangedListener(watcher)
    }

    private fun onBackButtonPressed() {
        val firstname = binding.firstnameEditText.text?.toString()
        val lastname = binding.lastnameEditText.text?.toString()
        val nickname = binding.loginEditText.text?.toString()
        val email = binding.emailEditText.text?.toString()
        val password = binding.passwordEditText.text?.toString()
        if (firstname.isNullOrBlank()
            && lastname.isNullOrBlank()
            && nickname.isNullOrBlank()
            && email.isNullOrBlank()
            && password.isNullOrBlank()
        ) {
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