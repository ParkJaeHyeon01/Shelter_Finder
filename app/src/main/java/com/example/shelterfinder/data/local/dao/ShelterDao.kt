// RoomDB에 저장된 수용시설(ShelterEntity) 데이터를 CRUD 처리하는 DAO 클래스
package com.example.shelterfinder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shelterfinder.data.local.entity.ShelterEntity

@Dao
interface ShelterDao {

    // 전체 저장 (기존 항목이 있으면 덮어쓰기): 수용시설 데이터를 일괄 삽입하며, 기존 데이터가 있으면 덮어씀 (REPLACE 전략)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shelters: List<ShelterEntity>)

    // 전체 삭제: 기존 DB 내용을 모두 삭제하여 초기화
    @Query("DELETE FROM shelters")
    suspend fun deleteAll()

    // 전체 조회: DB에 저장된 수용시설 전체 목록 반환 (초기 앱 접속 시 리스트에 사용)
    @Query("SELECT * FROM shelters")
    suspend fun getAllShelters(): List<ShelterEntity>

    // 이름이나 주소 일부로 검색: 시설명, 도로명 주소, 지번 주소 중 하나라도 키워드가 포함되면 해당 결과 반환
    @Query("""
        SELECT * FROM shelters 
        WHERE name LIKE '%' || :keyword || '%' 
           OR roadAddress LIKE '%' || :keyword || '%'
           OR jibunAddress LIKE '%' || :keyword || '%'
    """)
    suspend fun searchShelters(keyword: String): List<ShelterEntity>

    // 위도/경도를 기준으로 반경 5km 이내 검색: 위도/경도 범위를 바탕으로 DB 필터링, 5km 반경 범위 계산에 활용
    @Query("""
    SELECT * FROM shelters
    WHERE ndmsLat IS NOT NULL AND ndmsLng IS NOT NULL
      AND ndmsLat BETWEEN :minLat AND :maxLat
      AND ndmsLng BETWEEN :minLng AND :maxLng
    """)
    suspend fun getSheltersInBounds(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double
    ): List<ShelterEntity>

    // 개별 수용시설 상세 조회: 수용시설 고유 ID(actcFcltSn)를 기준으로 상세 정보 조회
    @Query("SELECT * FROM shelters WHERE actcFcltSn = :id LIMIT 1")
    suspend fun getShelterById(id: String): ShelterEntity?
}