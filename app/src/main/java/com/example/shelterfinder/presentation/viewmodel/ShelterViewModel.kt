package com.example.shelterfinder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelterfinder.data.local.entity.ShelterEntity
import com.example.shelterfinder.data.repository.ShelterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

class ShelterViewModel(
    private val repository: ShelterRepository
) : ViewModel() {

    private val _shelters = MutableStateFlow<List<ShelterEntity>>(emptyList())
    val shelters: StateFlow<List<ShelterEntity>> = _shelters

    // DB에 저장된 전체 ShelterEntity를 로드하여 _shelters에 할당
    fun loadAll() {
        viewModelScope.launch {
            _shelters.value = repository.getAll()
        }
    }

    // 키워드 기반 검색 수행 후 _shelters에 결과 할당
    fun search(keyword: String) {
        viewModelScope.launch {
            _shelters.value = repository.search(keyword)
        }
    }

    // 키워드 기반 검색을 단건 요청 형태로 반환
    suspend fun searchShelters(keyword: String): List<ShelterEntity> {
        return repository.search(keyword)
    }

    // 주어진 위경도를 기준으로 반경 5km 범위를 계산하여 (minLat, maxLat), (minLng, maxLng) 형태로 반환
    private fun getBounds(lat: Double, lng: Double, radiusKm: Double): Pair<Pair<Double, Double>, Pair<Double, Double>> {
        val earthRadius = 6371.0 // 지구 반지름 (km)

        val latDelta = Math.toDegrees(radiusKm / earthRadius)
        val lngDelta = Math.toDegrees(radiusKm / (earthRadius * cos(Math.toRadians(lat))))

        val minLat = lat - latDelta
        val maxLat = lat + latDelta
        val minLng = lng - lngDelta
        val maxLng = lng + lngDelta

        return Pair(minLat to maxLat, minLng to maxLng)
    }

    // Haversine 공식을 통해 두 지점 간의 실제 거리(km)를 계산
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0  // 지구 반지름 (km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    // 위도/경도 범위 기반 수용시설 조회
    fun loadNearby(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double) {
        viewModelScope.launch {
            _shelters.value = repository.getNearby(minLat, maxLat, minLng, maxLng)
        }
    }

    // 현재 위치 기반 반경 5km 이내 수용시설 필터링 및 거리순 정렬 후 _shelters에 저장
    fun loadNearbyShelters(currentLat: Double, currentLng: Double) {
        viewModelScope.launch {
            val radiusKm = 5.0
            val (latRange, lngRange) = getBounds(currentLat, currentLng, radiusKm)
            val (minLat, maxLat) = latRange
            val (minLng, maxLng) = lngRange

            val shelters = repository.getNearby(minLat, maxLat, minLng, maxLng)
            val filteredAndSorted = shelters
                .filter { it.ndmsLat != null && it.ndmsLng != null &&
                        calculateDistance(currentLat, currentLng, it.ndmsLat, it.ndmsLng) <= radiusKm }
                .sortedBy { calculateDistance(currentLat, currentLng, it.ndmsLat!!, it.ndmsLng!!) }

            _shelters.value = filteredAndSorted
        }
    }

    // 새로운 ShelterEntity 리스트로 DB 초기화 후 상태 갱신
    fun refreshShelters(newList: List<ShelterEntity>) {
        viewModelScope.launch {
            repository.clear()
            repository.insertShelters(newList)
            _shelters.value = repository.getAll()
        }
    }
}