// 앱 실행 후, 로컬 SharedPreferences를 기반으로 당일 데이터 갱신 여부를 확인하고,
// 필요 시 공공 수용시설 API를 호출하여 최신 데이터를 RoomDB에 저장한 뒤 메인 화면으로 진입하는 초기 갱신 로직을 담당
package com.example.shelterfinder.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shelterfinder.R
import com.example.shelterfinder.ShelterApplication
import com.example.shelterfinder.data.mapper.toEntity // ✅ 추가
import com.example.shelterfinder.data.remote.RetrofitInstance
import com.example.shelterfinder.data.remote.ShelterApiService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import androidx.core.content.edit
import com.example.shelterfinder.data.remote.dto.ShelterDto
import java.util.*
import com.example.shelterfinder.BuildConfig

class UpdateCheckFragment : Fragment() {

    // SharedPreferences에서 오늘 날짜와 비교하여 갱신 여부 확인
    private fun wasUpdatedToday(context: Context): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val prefs = context.getSharedPreferences("updatePrefs", Context.MODE_PRIVATE)
        return prefs.getString("lastUpdatedDate", "") == today
    }

    // SharedPreferences에 오늘 날짜 저장 (갱신 완료 기록용)
    private fun setTodayAsUpdated(context: Context) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.getSharedPreferences("updatePrefs", Context.MODE_PRIVATE).edit {
            putString("lastUpdatedDate", today)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val txtStatus = view.findViewById<TextView>(R.id.txtUpdateStatus)

        if (wasUpdatedToday(requireContext())) {
            txtStatus.text = "오늘 데이터는 이미 최신입니다."
            btnUpdate.text = "바로 시작하기"
        } else {
            txtStatus.text = "오늘 데이터가 갱신되지 않았습니다."
            btnUpdate.text = "데이터 갱신 후 시작"
        }

        btnUpdate.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (!wasUpdatedToday(requireContext())) {
                    // 공공 수용시설 API 호출 준비
                    val api = RetrofitInstance.retrofit.create(ShelterApiService::class.java)
                    // API로부터 전체 수용시설 리스트 수신
                    val response = api.getShelters(serviceKey = BuildConfig.PUBLIC_DATA_KEY)
                    // ShelterDto -> ShelterEntity 매핑
                    val entities = response.list.map { it.toEntity() }
                    val app = requireActivity().application as ShelterApplication
                    val repository = app.shelterRepository
                    // 기존 DB 초기화 후 새 데이터 저장
                    repository.clear()
                    repository.insertShelters(entities)
                    setTodayAsUpdated(requireContext())
                }
                // 메인 화면으로 이동
                findNavController().navigate(R.id.action_updateCheck_to_list)
            }
        }
    }
}