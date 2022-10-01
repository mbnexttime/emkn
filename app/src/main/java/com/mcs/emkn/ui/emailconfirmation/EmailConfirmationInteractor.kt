package com.mcs.emkn.ui.emailconfirmation

import kotlinx.coroutines.flow.Flow

interface EmailConfirmationInteractor {
    val errors: Flow<EmailConfirmationError>

    val navEvents: Flow<EmailConfirmationNavEvent>

    /**
     * По запросу loadTimer отдает время в секундах до следующей возможности запросить код
     */
    val timer: Flow<Long>

    fun validateCode(code: String)

    fun sendAnotherCode()

    fun loadTimer()
}