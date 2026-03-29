// ShelterViewModelFactory.kt는 ShelterViewModel에 필요한 Repository 의존성을
// 주입하기 위해 사용하는 ViewModel 생성 팩토리 클래스
package com.example.shelterfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shelterfinder.data.repository.ShelterRepository

class ShelterViewModelFactory(
    private val repository: ShelterRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShelterViewModel::class.java)) {
            return ShelterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}