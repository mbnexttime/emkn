package com.mcs.emkn.ui.changepassword

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcs.emkn.R
import com.mcs.emkn.core.RouterImpl
import com.mcs.emkn.databinding.FragmentConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordConfirmationFragment : Fragment() {
    private lateinit var binding : FragmentConfirmationBinding

    @Inject
    lateinit var router: RouterImpl

    private var verificationCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        setupLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendCodeButton.setOnClickListener {
            router.goToCommitChangePasswordScreen()
        }
        binding.backButton.setOnClickListener {
            onBackButtonPressed()
        }
        setupCodeEditField()
    }

    private fun setupLayout() {
        binding.confirmationHeader.text = resources.getString(R.string.change_password_confirmation_header)
    }

    private fun setupCodeEditField() {
        binding.sendCodeButton.isEnabled = false
        binding.codeEditText.onVerificationCodeFilledChangeListener = { isFilled ->
            binding.sendCodeButton.isEnabled = isFilled
        }
        binding.codeEditText.onVerificationCodeFilledListener = { code ->
            verificationCode = code
        }
    }

    private fun onBackButtonPressed() {
        if (binding.codeEditText.isBlank()) {
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