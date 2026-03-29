package com.example.shelterfinder

import android.app.Application
import android.util.Log
import com.example.shelterfinder.data.local.AppDatabase
import com.example.shelterfinder.data.repository.ShelterRepository
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk

class ShelterApplication : Application() {
    // DB 접근을 위한 ShelterRepository를 전역 변수로 선언
    lateinit var shelterRepository: ShelterRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(this) // Room Database 싱글톤 생성
        shelterRepository = ShelterRepository(database.shelterDao()) // DAO 획득 후 Repository에 주입
    }
}