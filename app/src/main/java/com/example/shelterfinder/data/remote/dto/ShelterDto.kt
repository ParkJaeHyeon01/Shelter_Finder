// 재난안전데이터공유플랫폼의 수용시설 API에서 수신한 JSON 응답 필드들을
// Kotlin 객체로 매핑하기 위한 DTO 클래스.
package com.example.shelterfinder.data.remote.dto

import com.example.shelterfinder.data.local.entity.ShelterEntity
import com.google.gson.annotations.SerializedName


data class ShelterDto(
    @SerializedName("ACTC_FCLT_SN") val actcFcltSn: String,

    @SerializedName("DSSTR_ACTC_FCLT_NM") val name: String?,
    @SerializedName("DADDR") val address: String?,
    @SerializedName("KORN_CTPV_NM") val ctprvnName: String?,
    @SerializedName("SGG_NM_KORN") val sggName: String?,
    @SerializedName("EMD_NM_KORN") val emdName: String?,
    @SerializedName("SGG_ROAD_NM") val sggRoadName: String?,
    @SerializedName("LOTNO_MNO") val lotNoMain: String?,
    @SerializedName("LOTNO_SNO") val lotNoSub: String?,
    @SerializedName("MLNO_YN") val isMountainLot: String?,
    @SerializedName("BMNO") val buildingMainNo: String?,
    @SerializedName("BSNO") val buildingSubNo: String?,

    @SerializedName("DSSTR_ACTC_PSBLTY_TNOP") val capacity: Int?,
    @SerializedName("FCAR") val areaSize: Double?,
    @SerializedName("ACTC_FCLT_DTL_CN") val detailInfo: String?,
    @SerializedName("USE_SE_CD") val isActive: String?,
    @SerializedName("ACTC_SE_CD") val isShelterEnabled: String?,

    @SerializedName("TLT_YN") val hasToilet: String?,
    @SerializedName("WASU_YN") val hasWater: String?,
    @SerializedName("MESE_FCTY_YN") val hasMeal: String?,
    @SerializedName("UDGD_YN") val isUnderground: String?,
    @SerializedName("ERQK_SHNT_YN") val isEarthquakeShelter: String?,
    @SerializedName("ETC_CNVN_FCLT_YN") val hasOtherFacilities: String?,

    @SerializedName("TELNO") val phone: String?,
    @SerializedName("MVMN_TELNO") val mobilePhone: String?,
    @SerializedName("FXNO") val fax: String?,
    @SerializedName("MNG_REPE_TELNO") val managerPhone: String?,

    @SerializedName("FRST_REG_DT") val createdAt: String?,
    @SerializedName("LAST_MDFCN_DT") val updatedAt: String?,
    @SerializedName("DSGN_YMD") val designatedDate: String?,

    @SerializedName("XCRD") val xCrd: Double?,
    @SerializedName("YCRD") val yCrd: Double?,
    @SerializedName("NDMS_LAT") val ndmsLat: Double?,
    @SerializedName("NDMS_LOT") val ndmsLng: Double?
)
