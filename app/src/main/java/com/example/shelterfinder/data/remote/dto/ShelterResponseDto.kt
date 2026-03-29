// 재난안전데이터공유플랫폼 API에서 수신되는 JSON 응답 중 최상위 body 필드를 매핑하는 DTO 클래스이다.
package com.example.shelterfinder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShelterResponseDto(
    @SerializedName("body")
    val list: List<ShelterDto>
)
