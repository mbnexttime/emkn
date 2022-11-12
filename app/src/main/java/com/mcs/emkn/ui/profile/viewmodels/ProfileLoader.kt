package com.mcs.emkn.ui.profile.viewmodels

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.database.Database
import com.mcs.emkn.database.entities.ProfileEntity
import com.mcs.emkn.network.Api
import com.mcs.emkn.network.dto.request.ProfilesGetRequestDto
import com.mcs.emkn.network.dto.response.ProfileDto
import com.mcs.emkn.ui.courses.viewmodels.CoursesViewModel
import javax.inject.Inject

class ProfileLoader @Inject constructor(
    private val db: Database,
    private val api: Api
) {
    suspend fun loadProfiles(
        profilesIds: List<Int>,
        profilesStorage: Map<Int, ProfileEntity>?
    ): LoadProfilesResult {
        try {
            when (val response = api.profilesGet(ProfilesGetRequestDto(profilesIds))) {
                is NetworkResponse.Success -> {
                    val storageAndUpdateResult = updateProfilesInDb(
                        response.body.profiles,
                        profilesIds,
                        profilesStorage
                    )
                    return LoadProfilesResult.Success(storageAndUpdateResult)
                }
                is NetworkResponse.ServerError -> {
                    return LoadProfilesResult.UnknownError(null)
                }
                is NetworkResponse.NetworkError -> {
                    return LoadProfilesResult.NetworkError
                }
                is NetworkResponse.UnknownError -> {
                   return LoadProfilesResult.UnknownError(response.error.message)
                }
            }
        } catch (e: Throwable) {
            return LoadProfilesResult.UnknownError(e.message)
        }
    }

    fun loadProfilesFromDb(
        profilesIds: List<Int>,
    ): Map<Int, ProfileEntity>? {
        return try {
                db.coursesDao().getProfilesByIds(profilesIds).groupBy { it.id }
                    .mapValues { (_, v) -> v.first() }
        } catch (_: Throwable) {
            null
        }
    }

    fun loadAllProfilesFromDb(): Map<Int, ProfileEntity>? {
        return try {
                db.coursesDao().getProfiles().groupBy { it.id }
                    .mapValues { (_, v) -> v.first() }
        } catch (_: Throwable) {
            null
        }
    }

    private fun updateProfilesInDb(
        profiles: List<ProfileDto>,
        profilesIds: List<Int>,
        profilesStorage: Map<Int, ProfileEntity>?
    ): Pair<Map<Int, ProfileEntity>?, Boolean> {
        val newStorage = profilesStorage?.toMutableMap() ?: mutableMapOf()
        profiles.forEach { profileDto ->
            newStorage.put(
                profileDto.id,
                ProfileEntity(
                    profileDto.id,
                    profileDto.avatarUrl,
                    profileDto.firstName,
                    profileDto.secondName
                )
            )
        }

        return try {
            db.coursesDao()
                .putProfiles(newStorage.entries.filter { entry -> entry.key in profilesIds }
                    .map { entry -> entry.value })
            newStorage to true
        } catch (e: Throwable) {
            newStorage to false
        }
    }

    sealed interface LoadProfilesResult {
        class Success(val storageAndUpdateResult: Pair<Map<Int, ProfileEntity>?, Boolean>) : LoadProfilesResult
        object NetworkError : LoadProfilesResult
        class UnknownError(val msg: String?) : LoadProfilesResult
    }
}