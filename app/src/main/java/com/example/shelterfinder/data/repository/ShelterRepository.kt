// ViewModel/Fragment와 DB 사이에서 데이터 접근 캡슐화
// ShelterDao를 통해 로컬 RoomDB에 접근하는 기능을 추상화한 Repository 클래스
package com.example.shelterfinder.data.repository

import com.example.shelterfinder.data.local.dao.ShelterDao
import com.example.shelterfinder.data.local.entity.ShelterEntity

class ShelterRepository(private val shelterDao: ShelterDao) {

    suspend fun insertShelters(shelters: List<ShelterEntity>) {
        shelterDao.insertAll(shelters)
    }

    suspend fun getAll(): List<ShelterEntity> {
        return shelterDao.getAllShelters()
    }

    suspend fun search(keyword: String): List<ShelterEntity> {
        return shelterDao.searchShelters(keyword)
    }

    suspend fun getNearby(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<ShelterEntity> {
        return shelterDao.getSheltersInBounds(minLat, maxLat, minLng, maxLng)
    }

    suspend fun clear() {
        shelterDao.deleteAll()
    }

    suspend fun getById(id: String): ShelterEntity? {
        return shelterDao.getShelterById(id)
    }
}