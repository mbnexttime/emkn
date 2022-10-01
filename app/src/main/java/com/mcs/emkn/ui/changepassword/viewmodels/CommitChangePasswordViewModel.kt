package com.mcs.emkn.ui.changepassword.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcs.emkn.database.Database
import com.mcs.emkn.network.Api
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class CommitChangePasswordViewModel @Inject constructor(
    private val api: Api,
    private val database: Database,
) : CommitChangePasswordInteractor, ViewModel() {
    override val errors: Flow<CommitChangePasswordError>
        get() = _errors
    override val navEvents: Flow<CommitChangePasswordNavEvent>
        get() = _navEvents

    private val _errors = MutableSharedFlow<CommitChangePasswordError>()
    private val _navEvents = MutableSharedFlow<CommitChangePasswordNavEvent>()

    private val submitNewPasswordAtomic = AtomicBoolean(false)

    override fun submitNewPassword(password: String) {
        if (submitNewPasswordAtomic.get()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (!submitNewPasswordAtomic.compareAndSet(false, true)) {
                return@launch
            }
            try {
                val commit = database.accountsDao().getChangePasswordCommits().firstOrNull() ?: return@launch
            } finally {
                submitNewPasswordAtomic.compareAndSet(true, false)
            }
        }
    }
}