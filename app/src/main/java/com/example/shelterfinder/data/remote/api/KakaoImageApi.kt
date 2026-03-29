// 카카오 개발자 플랫폼에서 제공하는 이미지 검색 API를 통해, 사용자가 선택한
// 수용시설에 대한 대표 이미지를 검색하는 REST API 인터페이스이다.
package com.example.shelterfinder.data.remote.api

import com.example.shelterfinder.data.remote.dto.KakaoImageResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoImageApi {
    @GET("v2/search/image")
    suspend fun searchImages(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 1
    ): KakaoImageResponse
}