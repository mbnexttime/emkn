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
import com.mcs.emkn.core.Router
import com.mcs.emkn.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding : FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButton.setOnClickListener {
            router.goToChangePasswordConfirmationScreen()
        }
        binding.backButton.setOnClickListener {
            onBackButtonPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackButtonPressed()
            this.isEnabled = true
        }
        subscribeToFormFields()
    }

    private fun decideSignInButtonEnabledState(login: String?) {
        binding.submitButton.isEnabled = !login.isNullOrBlank()
    }
    private fun subscribeToFormFields() {
        decideSignInButtonEnabledState(
            login = binding.editText.text?.toString(),
        )
        binding.editText.doAfterTextChanged { login ->
            decideSignInButtonEnabledState(
                login = login?.toString(),
            )
        }
    }

    private fun onBackButtonPressed() {
        val login = binding.editText.text?.toString()
        if (login.isNullOrBlank()) {
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