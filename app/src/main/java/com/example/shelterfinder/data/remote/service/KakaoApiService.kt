// Kakao Developers의 이미지 검색 API를 Retrofit2를 통해 호출하는 서비스 인터페이스
package com.example.shelterfinder.data.remote.service

import com.example.shelterfinder.data.remote.api.KakaoImageApi
import com.example.shelterfinder.data.remote.dto.KakaoImageResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApiService {
    @GET("v2/search/image")
    suspend fun searchImage(
        @Header("Authorization") authorization: String,
        @Query("query") query: String
    ): KakaoImageResponse
}