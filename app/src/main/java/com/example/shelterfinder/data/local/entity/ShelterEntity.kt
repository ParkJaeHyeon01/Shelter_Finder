// Room Database에 저장되는 수용시설의 모든 정보를 정의하는 데이터 클래스다.
package com.example.shelterfinder.data.local.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey val actcFcltSn: String, // 수용시설 고유번호

    val name: String,                  // 시설명
    val address: String?,              // 상세주소 (도로명 + 지번 결합 가능)
    val ctprvnName: String?,           // 시도명
    val sggName: String?,              // 시군구명
    val emdName: String?,              // 읍면동명
    val sggRoadName: String?,          // 시군구 도로명
    val lotNoMain: String?,            // 지번본번
    val lotNoSub: String?,             // 지번부번
    val isMountainLot: Boolean?,       // 산번지여부
    val buildingMainNo: String?,       // 건물본번
    val buildingSubNo: String?,        // 건물부번

    val roadAddress: String?,      // 도로명 주소(조합)
    val jibunAddress: String?,     // 지번 주소(조합)

    val capacity: Int?,                // 수용가능인원
    val areaSize: Double?,             // 시설면적
    val detailInfo: String?,           // 상세내용
    val isActive: Boolean?,          // 사용구분코드
    val isShelterEnabled: Boolean?,      // 수용구분코드

    val hasToilet: Boolean?,           // 화장실 여부
    val hasWater: Boolean?,            // 급수 여부
    val hasMeal: Boolean?,             // 급식시설 여부
    val isUnderground: Boolean?,       // 지하여부
    val isEarthquakeShelter: Boolean?, // 지진대피여부
    val hasOtherFacilities: Boolean?,  // 기타편의시설

    val phone: String?,                // 전화번호
    val mobilePhone: String?,          // 이동전화번호
    val fax: String?,                  // 팩스번호
    val managerPhone: String?,         // 관리책임자 전화번호

    val createdAt: String?,            // 최초등록일시
    val updatedAt: String?,            // 최종수정일시
    val designatedDate: String?,       // 지정일자

    val xCrd: Double?,                 // X좌표
    val yCrd: Double?,                 // Y좌표
    val ndmsLat: Double?,              // NDMS 위도
    val ndmsLng: Double?,               // NDMS 경도

    var isExpanded: Boolean = false // 뷰 확장 여부
) {
    @Ignore
    var distanceFromCurrent: Double? = null
}
