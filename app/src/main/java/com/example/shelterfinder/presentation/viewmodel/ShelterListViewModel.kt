// Kakao 이미지 검색 API를 통해 수용시설 관련 외관 이미지를 요청하고,
// 그 결과로부터 이미지 URL을 추출하여 UI(Fragment)에 전달
package com.example.shelterfinder.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.shelterfinder.data.remote.service.KakaoApiService
import com.example.shelterfinder.data.remote.dto.KakaoImageResponse
import kotlinx.coroutines.launch
import com.example.shelterfinder.BuildConfig

class ShelterListViewModel(
    private val kakaoApiService: KakaoApiService
) : ViewModel() {

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    fun loadShelterImage(query: String) {
        viewModelScope.launch {
            try {
                val result: KakaoImageResponse = kakaoApiService.searchImage(
                    authorization = "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}",
                    query = query
                )

                Log.d("KAKAO_IMAGE", "응답 문서 수: ${result.documents.size}")
                result.documents.forEachIndexed { index, doc ->
                    Log.d("KAKAO_IMAGE", "[$index] imageUrl=${doc.imageUrl}")
                }
                _imageUrl.value = result.documents.firstOrNull()?.imageUrl ?: ""
            } catch (e: Exception) {
                Log.e("KAKAO_IMAGE", "이미지 검색 실패: ${e.message}", e)
                _imageUrl.value = ""
            }
        }
    }
}