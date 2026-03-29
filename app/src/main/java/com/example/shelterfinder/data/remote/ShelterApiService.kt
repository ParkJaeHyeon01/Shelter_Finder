// 수용시설 정보 API를 호출하기 위한 Retrofit2 인터페이스
// 공공 데이터를 요청하고, 그 결과를 ShelterResponseDto로 반환받는다.
package com.example.shelterfinder.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.shelterfinder.data.remote.dto.ShelterResponseDto

interface ShelterApiService {
    @GET("DSSP-IF-10073")
    suspend fun getShelters(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 5000,
        @Query("returnType") returnType: String = "json"
    ): ShelterResponseDto
}