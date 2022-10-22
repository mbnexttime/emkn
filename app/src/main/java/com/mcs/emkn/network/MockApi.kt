package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.errorresponse.*
import com.mcs.emkn.network.dto.request.*
import com.mcs.emkn.network.dto.response.*
import retrofit2.Response

class MockApi : Api {
    override suspend fun accountsRegister(request: RegistrationRequestDto): NetworkResponse<ResponseWithTokenAndTimeDto, RegistrationErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun validateEmail(request: ValidateEmailRequestDto): NetworkResponse<Unit, ValidateEmailErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsLogin(request: LoginRequestDto): NetworkResponse<Unit, LoginErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsBeginChangePassword(request: BeginChangePasswordRequestDto): NetworkResponse<ResponseWithTokenAndTimeDto, BeginChangePasswordErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsValidateChangePassword(request: ValidateChangePasswordRequestDto): NetworkResponse<TokenDto, ValidateChangePasswordErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsCommitChangePassword(request: CommitChangePasswordRequestDto): NetworkResponse<Unit, CommitChangePasswordErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsRevalidateRegistrationCredentials(request: RevalidateCredentialsDto): NetworkResponse<ResponseWithTokenAndTimeDto, RevalidateRegistrationCredentialsErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsRevalidateChangePasswordCredentials(request: RevalidateCredentialsDto): NetworkResponse<Unit, RevalidateRegistrationCredentialsErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun coursesPeriods(): NetworkResponse<CoursesPeriodsResponseDto, Unit> {
        return NetworkResponse.Success(
            CoursesPeriodsResponseDto(
                listOf(
                    PeriodDto(1, "2022"),
                    PeriodDto(2, "2021"),
                    PeriodDto(3, "2029")
                )
            ),
            Response.success(200, "OK")
        )
    }

    override suspend fun coursesList(request: CoursesListRequestDto): NetworkResponse<CoursesListResponseDto, CoursesListErrorResponseDto> {
        return NetworkResponse.Success(
            CoursesListResponseDto(
                listOf(
                    PeriodCoursesDto(
                        1, listOf(
                            CourseDto(
                                12, "C++", true, "lala", listOf(1, 2)
                            )
                        )
                    )
                )
            ), Response.success(200, "OK")
        )
    }

    override suspend fun coursesEnroll(request: CoursesEnrollUnenrollRequestDto): NetworkResponse<Unit, CoursesEnrollErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun coursesUnenroll(request: CoursesEnrollUnenrollRequestDto): NetworkResponse<Unit, CoursesUnenrollErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun profilesGet(request: ProfilesGetRequestDto): NetworkResponse<ProfilesGetResponseDto, Unit> {
        return NetworkResponse.Success(
            ProfilesGetResponseDto(
                listOf(
                    ProfileDto(
                        1,
                        "a.com",
                        "Joe",
                        "Biden"
                    ), ProfileDto(2, "b.com", "Rick", "Astley")
                )
            ), Response.success(200, "OK")
        )
    }
}