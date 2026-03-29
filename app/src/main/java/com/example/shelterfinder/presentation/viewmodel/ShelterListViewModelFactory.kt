// ShelterListViewModel을 생성하기 위한 커스텀 ViewModelProvider.Factory 클래스.
// KakaoApiService를 의존성으로 주입받아 ViewModel을 생성한다.
package com.example.shelterfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shelterfinder.data.remote.service.KakaoApiService

class ShelterListViewModelFactory(
    private val kakaoApiService: KakaoApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShelterListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShelterListViewModel(kakaoApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}