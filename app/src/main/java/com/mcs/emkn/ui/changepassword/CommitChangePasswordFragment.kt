package com.mcs.emkn.ui.changepassword

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.RouterImpl
import com.mcs.emkn.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class CommitChangePasswordFragment : Fragment() {
    private lateinit var binding : FragmentForgotPasswordBinding

    @Inject
    lateinit var router: RouterImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        setupLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            onBackButtonPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackButtonPressed()
            this.isEnabled = true
        }
        subscribeToFormFields()
    }

    private fun setupLayout() {
        binding.passwordForgotDescription.text = resources.getString(R.string.commit_change_password_description)
        binding.submitButton.text = resources.getString(R.string.commit_change_password_submit_button)
        binding.emailTextInputLayout.hint = resources.getString(R.string.commit_change_password_hint_text)
    }

    private fun decideSignInButtonEnabledState(email: String?) {
        binding.submitButton.isEnabled = !email.isNullOrBlank()
    }
    private fun subscribeToFormFields() {
        decideSignInButtonEnabledState(
            email = binding.emailEditText.text?.toString(),
        )
        binding.emailEditText.doAfterTextChanged { email ->
            decideSignInButtonEnabledState(
                email = email?.toString(),
            )
        }
    }

    private fun onBackButtonPressed() {
        val email = binding.emailEditText.text?.toString()
        if (email.isNullOrBlank()) {
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