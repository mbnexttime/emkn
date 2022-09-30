package com.mcs.emkn.ui.changepassword.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.database.Database
import com.mcs.emkn.database.entities.ChangePasswordAttempt
import com.mcs.emkn.database.entities.ChangePasswordCommit
import com.mcs.emkn.network.Api
import com.mcs.emkn.network.dto.request.BeginChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.ValidateChangePasswordRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val api: Api,
    private val db: Database,
) : ViewModel(), ChangePasswordInteractor {
    override val errors: Flow<ChangePasswordError>
        get() = TODO("Not yet implemented")
    override val navEvents: Flow<ChangePasswordNavEvent>
        get() = TODO("Not yet implemented")
    override val timerFlow: Flow<Long>
        get() = TODO("Not yet implemented")

    private val _errors = MutableSharedFlow<ChangePasswordError>()
    private val _navEvents = MutableSharedFlow<ChangePasswordNavEvent>()
    private val _timerFlow = MutableSharedFlow<Long>()
    private val isSendingCodeAtomic = AtomicBoolean(false)
    private val isValidatingCodeAtomic = AtomicBoolean(false)

    override fun validateCode(code: String) {
        if (isValidatingCodeAtomic.get()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!isValidatingCodeAtomic.compareAndSet(false, true)) {
                return@launch
            }
            try {
                val attempt = db.accountsDao().getChangePasswordAttempts().firstOrNull() ?: return@launch
                val response =
                    api.accountsValidateChangePassword(ValidateChangePasswordRequestDto(attempt.randomToken, code))
                when (response) {
                    is NetworkResponse.Success -> {
                        db.runInTransaction {
                            db.accountsDao().deleteChangePasswordCommits()
                            db.accountsDao()
                                .putChangePasswordCommit(ChangePasswordCommit(response.body.changePasswordToken))
                        }
                        _navEvents.emit(ChangePasswordNavEvent.ContinueChangePassword)
                    }
                    is NetworkResponse.ServerError -> {
                        val errorsBody = response.body
                        if (errorsBody != null) {
                            if (errorsBody.errors.codeInvalid != null) {
                                _errors.emit(ChangePasswordError.InvalidCode)
                            }
                            if (errorsBody.errors.passwordChangeExpired != null) {
                                _errors.emit(ChangePasswordError.CodeExpired)
                            }
                        }
                    }
                    is NetworkResponse.NetworkError -> _errors.emit(ChangePasswordError.BadNetwork)
                    is NetworkResponse.UnknownError -> Unit
                }
            } finally {
                isValidatingCodeAtomic.compareAndSet(true, false)
            }
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
                val attempt = db.accountsDao().getChangePasswordAttempts().firstOrNull() ?: return@launch
                val response = api.accountsBeginChangePassword(BeginChangePasswordRequestDto(attempt.credentials))
                when (response) {
                    is NetworkResponse.Success -> {
                        val newAttempt = ChangePasswordAttempt(
                            credentials = attempt.credentials,
                            randomToken = response.body.randomToken,
                            expiresInSeconds = response.body.expiresIn.toLong(),
                            createdAt = System.currentTimeMillis(),
                        )
                        db.runInTransaction {
                            db.accountsDao().deleteChangePasswordAttempts()
                            db.accountsDao().putChangePasswordAttempt(newAttempt)
                        }
                        _timerFlow.emit(newAttempt.expiresInSeconds)
                    }
                    is NetworkResponse.ServerError -> Unit
                    is NetworkResponse.NetworkError -> _errors.emit(ChangePasswordError.BadNetwork)
                    is NetworkResponse.UnknownError -> Unit
                }
            } finally {
                isSendingCodeAtomic.compareAndSet(true, false)
            }
        }
    }

    override fun loadTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            val attempt = db.accountsDao().getChangePasswordAttempts().firstOrNull() ?: return@launch
            _timerFlow.emit(attempt.expiresInSeconds - (System.currentTimeMillis() - attempt.createdAt) / 1000)
        }
    }
}