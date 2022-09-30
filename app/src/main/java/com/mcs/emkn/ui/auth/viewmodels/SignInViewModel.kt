package com.mcs.emkn.ui.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.database.Database
import com.mcs.emkn.database.entities.Credentials
import com.mcs.emkn.network.Api
import com.mcs.emkn.network.dto.request.LoginRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val api: Api,
    private val db: Database,
    private val observer: SignInSignUpQueriesObserver,
) : SignInInteractor, ViewModel() {
    override val isLoadingFlow: Flow<Boolean>
        get() = _isLoadingFlow
    override val errorsFlow: Flow<SignInError>
        get() = _errorsFlow
    override val navEvents: Flow<SignInNavEvent>
        get() = _navEvents

    private val _isLoadingFlow = MutableStateFlow(false)
    private val _navEvents = MutableSharedFlow<SignInNavEvent>()
    private val _errorsFlow = MutableSharedFlow<SignInError>()
    private val isLoadingAtomic = observer.isLoading

    override fun onSignInClick(login: String, password: String) {
        if (isLoadingAtomic.get()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!isLoadingAtomic.compareAndSet(false, true)) {
                return@launch
            }
            try {
                _isLoadingFlow.emit(true)
                when (val response = api.accountsLogin(LoginRequestDto(login, password))) {
                    is NetworkResponse.Success -> {
                        db.runInTransaction {
                            db.accountsDao().deleteCredentials()
                            db.accountsDao().putCredentials(Credentials(login, password))
                        }
                        _navEvents.emit(SignInNavEvent.ContinueSignIn)
                    }
                    is NetworkResponse.ServerError -> {
                        val errorsBody = response.body
                        if (errorsBody != null) {
                            if (errorsBody.errors.illegalLoginOrEmail != null) {
                                _errorsFlow.emit(SignInError.IncorrectLoginOrPassword)
                            }
                        }
                    }
                    is NetworkResponse.NetworkError -> _errorsFlow.emit(SignInError.BadNetwork)
                    is NetworkResponse.UnknownError -> Unit
                }
                _isLoadingFlow.emit(false)
            } finally {
                isLoadingAtomic.compareAndSet(true, false)
            }
        }
    }
}