package com.mcs.emkn.ui.emailconfirmation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.database.Database
import com.mcs.emkn.network.Api
import com.mcs.emkn.network.dto.request.RevalidateCredentialsDto
import com.mcs.emkn.network.dto.request.ValidateEmailRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class EmailConfirmationViewModel @Inject constructor(
    private val api: Api,
    private val db: Database,
) : EmailConfirmationInteractor, ViewModel() {
    override val errors: Flow<EmailConfirmationError>
        get() = _errors
    override val navEvents: Flow<EmailConfirmationNavEvent>
        get() = _navEvents
    override val timer: Flow<Long>
        get() = _timer

    private val _errors = MutableSharedFlow<EmailConfirmationError>()
    private val _navEvents = MutableSharedFlow<EmailConfirmationNavEvent>()
    private val _timer = MutableSharedFlow<Long>()

    private val isValidatingAtomic = AtomicBoolean(false)
    private val isSendingCodeAtomic = AtomicBoolean(false)

    override fun validateCode(code: String) {
        if (isValidatingAtomic.get()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!isValidatingAtomic.compareAndSet(false, true)) {
                return@launch
            }
            try {
                val attempt = db.accountsDao().getSignUpAttempts().firstOrNull() ?: return@launch
                val response = api.validateEmail(ValidateEmailRequestDto(code, attempt.randomToken))
                when (response) {
                    is NetworkResponse.Success -> {
                        db.accountsDao().deleteSignUpAttempts()
                        _navEvents.emit(EmailConfirmationNavEvent.ContinueConfirmation)
                    }
                    is NetworkResponse.ServerError -> {
                        val errorsBody = response.body
                        if (errorsBody != null) {
                            if (errorsBody.errors.codeInvalid != null) {
                                _errors.emit(EmailConfirmationError.InvalidCode)
                            }
                            if (errorsBody.errors.registrationExpired != null) {
                                _errors.emit(EmailConfirmationError.CodeExpired)
                            }
                        }
                    }
                    is NetworkResponse.NetworkError -> _errors.emit(EmailConfirmationError.BadNetwork)
                    is NetworkResponse.UnknownError -> Unit
                }
            } finally {
                isValidatingAtomic.compareAndSet(true, false)
            }
        }
    }

    override fun loadTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            val attempt = db.accountsDao().getSignUpAttempts().firstOrNull() ?: return@launch
            _timer.emit(attempt.expiresInSeconds - (System.currentTimeMillis() - attempt.createdAt) / 1000)
        }
    }

    override fun sendAnotherCode() {
        if (isSendingCodeAtomic.get()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!isSendingCodeAtomic.compareAndSet(false, true)) {
                return@launch
            }
            try {
                val attempt = db.accountsDao().getSignUpAttempts().firstOrNull() ?: return@launch
                val response = api.accountsRevalidateCredentials(RevalidateCredentialsDto(attempt.randomToken))
                when (response) {
                    is NetworkResponse.Success -> {
                        val newAttempt = attempt.copy(
                            randomToken = response.body.tokenAndTimeDto.randomToken,
                            expiresInSeconds = response.body.tokenAndTimeDto.expiresIn.toLong(),
                            createdAt = System.currentTimeMillis(),
                        )
                        db.runInTransaction {
                            db.accountsDao().deleteSignUpAttempts()
                            db.accountsDao().putSignUpAttempt(newAttempt)
                        }
                        _timer.emit(newAttempt.expiresInSeconds)
                    }
                    is NetworkResponse.ServerError -> Unit
                    is NetworkResponse.NetworkError -> _errors.emit(EmailConfirmationError.BadNetwork)
                    is NetworkResponse.UnknownError -> Unit
                }
            } finally {
                isSendingCodeAtomic.compareAndSet(true, false)
            }
        }
    }
}