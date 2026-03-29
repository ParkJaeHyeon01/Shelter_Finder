// 외부 API로부터 받아온 ShelterDto 데이터를 앱 내에서 사용하는 ShelterEntity로 변환하기 위한 매핑 로직을 수행한다.
package com.example.shelterfinder.data.mapper

import com.example.shelterfinder.data.local.entity.ShelterEntity
import com.example.shelterfinder.data.remote.dto.ShelterDto

fun ShelterDto.buildRoadAddress(): String {
    // 도로명 주소가 없거나 본번-부번만 있는 경우 주소 정보 없음 반환
    if (ctprvnName.isNullOrBlank() && sggName.isNullOrBlank() && sggRoadName.isNullOrBlank()) {
        return "주소 정보 제공되지 않음"
    }

    val roadAddress = listOfNotNull(
        ctprvnName?.trim(),
        sggName?.trim(),
        sggRoadName?.trim()
    ).joinToString(" ")

    // 본번-부번만 존재하면 주소 정보 없음
    return if (roadAddress.isNotBlank()) {
        roadAddress
    } else {
        "주소 정보 없음"
    }
}

fun ShelterDto.buildJibunAddress(): String {
    // 지번 주소가 없거나 본번-부번만 있는 경우 주소 정보 없음 반환
    if (ctprvnName.isNullOrBlank() && sggName.isNullOrBlank() && emdName.isNullOrBlank()) {
        return "주소 정보 제공되지 않음"
    }

    val jibunAddress = listOfNotNull(
        ctprvnName?.trim(),
        sggName?.trim(),
        emdName?.trim()
    ).joinToString(" ")

    // 본번-부번만 존재하면 주소 정보 없음
    return if (jibunAddress.isNotBlank()) {
        jibunAddress
    } else {
        "주소 정보 없음"
    }
}

private fun formatNumber(main: String?, sub: String?): String? {
    return when {
        main.isNullOrBlank() -> null
        sub.isNullOrBlank() || sub == "0" -> main
        else -> "$main-$sub"
    }
}

fun ShelterDto.toEntity(): ShelterEntity {
    val road = buildRoadAddress()
    val jibun = buildJibunAddress()

    return ShelterEntity(
        actcFcltSn = actcFcltSn,
        name = name ?: "이름 없음",
        address = address,
        ctprvnName = ctprvnName,
        sggName = sggName,
        emdName = emdName,
        sggRoadName = sggRoadName,
        lotNoMain = lotNoMain,
        lotNoSub = lotNoSub,
        isMountainLot = isMountainLot == "1",
        buildingMainNo = buildingMainNo,
        buildingSubNo = buildingSubNo,
        capacity = capacity,
        areaSize = areaSize,
        detailInfo = detailInfo,
        isActive = isActive == "1",
        isShelterEnabled = isShelterEnabled == "1",
        hasToilet = hasToilet == "1",
        hasWater = hasWater == "1",
        hasMeal = hasMeal == "1",
        isUnderground = isUnderground == "1",
        isEarthquakeShelter = isEarthquakeShelter == "Y",
        hasOtherFacilities = hasOtherFacilities == "1",
        phone = phone ?: "제공되지 않음",
        mobilePhone = mobilePhone ?: "제공되지 않음",
        fax = fax ?: "제공되지 않음",
        managerPhone = managerPhone ?: "제공되지 않음",
        createdAt = createdAt,
        updatedAt = updatedAt,
        designatedDate = designatedDate ?: "제공되지 않음",
        xCrd = xCrd,
        yCrd = yCrd,
        ndmsLat = ndmsLat,
        ndmsLng = ndmsLng,
        roadAddress = road,
        jibunAddress = jibun
    )
}