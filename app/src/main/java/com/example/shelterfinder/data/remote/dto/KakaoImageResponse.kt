// 카카오 이미지 검색 API의 JSON 응답을 앱 내부에서 사용하기 위한 데이터 전용 클래스 (DTO).
// documents라는 JSON 배열 안의 이미지 URL 리스트를 파싱하며,
// 이 중 첫 번째 이미지를 수용시설 상세 정보 화면에서 대표 이미지로 활용한다.
package com.example.shelterfinder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class KakaoImageResponse(
    @SerializedName("documents") val documents: List<KakaoImageDocument>
)

data class KakaoImageDocument(
    @SerializedName("image_url") val imageUrl: String
)
