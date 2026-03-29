// 앱 실행 시 가장 먼저 표시되는 스플래시 화면(Fragment)으로
// 약간의 로딩 딜레이(1.5초)를 준 뒤 자동으로 UpdateCheckFragment 또는 메인 화면으로 전환
package com.example.shelterfinder.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shelterfinder.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 간단한 로딩 딜레이 후 다음 화면으로 이동
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1500)
            findNavController().navigate(R.id.action_splash_to_updateCheck)
        }
    }
}