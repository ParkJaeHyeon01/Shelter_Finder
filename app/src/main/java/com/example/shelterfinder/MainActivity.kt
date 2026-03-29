package com.example.shelterfinder

import android.os.Bundle
//import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.shelterfinder.presentation.ui.ShelterListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 시스템 UI (상단/하단 바 등) 아래까지 콘텐츠 표시
        setContentView(R.layout.activity_main)

        // 시스템 바 높이만큼 패딩 적용하여 UI 밀림 방지
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // XML 내 nav_host_fragment_container ID를 기준으로 Navigation 컨트롤러 획득
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        // 추후 프래그먼트 간 이동에 사용될 NavController 참조
        val navController = navHostFragment.navController
    }
}