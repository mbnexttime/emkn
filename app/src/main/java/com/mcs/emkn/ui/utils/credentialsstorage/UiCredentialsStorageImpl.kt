package com.mcs.emkn.ui.utils.credentialsstorage

class UiCredentialsStorageImpl() : UiCredentialsStorage {
    override var login: String = ""
        get() {
            val res = field
            login = ""
            return res
        }
        set(value) { field = value}
    override var password: String = ""
        get() {
            val res = field
            password = ""
            return res
        }
        set(value) { field = value}
}