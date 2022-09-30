package com.mcs.emkn.ui.emailconfirmation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcs.emkn.R
import com.mcs.emkn.core.Router
import com.mcs.emkn.databinding.FragmentConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmailConfirmationFragment : Fragment() {
    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var router: Router
    private var verificationCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            onBackButtonPressed()
        }
        setupCodeEditField()
    }

    private fun setupCodeEditField() {
        binding.sendCodeButton.isEnabled = false
        binding.codeEditText.onVerificationCodeChangeListener = { isFilled ->
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
            .setTitle(R.string.confirmation_back_alert_dialog_text)
            .setNegativeButton(R.string.sign_in_back_alert_dialog_cancel_button_text) { dialog, _ ->
                dialog?.dismiss()
            }
            .setPositiveButton(R.string.sign_in_back_alert_dialog_ok_button_text) { _, _ ->
                router.back()
            }
            .show()
    }
}