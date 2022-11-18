package com.mcs.emkn.ui.profile.viewmodels

import android.net.Uri
import retrofit2.http.Url

data class Profile(
    val id: Int,
    val avatarUri: Uri,
    val firstName: String,
    val secondName: String,
)