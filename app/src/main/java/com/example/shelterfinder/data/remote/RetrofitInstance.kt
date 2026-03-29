// 재난안전데이터공유플랫폼 API 호출을 위한 Retrofit 인스턴스를 생성하고
// 앱 전역에서 공유할 수 있도록 싱글톤 객체로 제공한다.
package com.example.shelterfinder.data.remote

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://www.safetydata.go.kr/V2/api/"

    private val gson = GsonBuilder().setLenient().create()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}