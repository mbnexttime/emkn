package com.mcs.emkn.network.dto.response

import com.mcs.emkn.network.dto.error.NetError

interface AuthResponseDto {
    val errors: List<NetError>
}