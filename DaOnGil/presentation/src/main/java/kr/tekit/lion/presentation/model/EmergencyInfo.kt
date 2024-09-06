package kr.tekit.lion.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.tekit.lion.domain.model.EmergencyMapInfo

@Parcelize
data class EmergencyInfo(
    val hospitalList: HospitalInfo?,
    val emergencyType: String,
    val emergencyId: String?,
    val aedList: AedInfo?
): Parcelable

fun EmergencyMapInfo.toEmergencyInfo(): EmergencyInfo {
    return EmergencyInfo(
        hospitalList = this.hospitalList?.toHospitalInfo(),
        emergencyType = this.emergencyType,
        emergencyId = this.emergencyId,
        aedList = this.aedList?.toAedInfo()
    )
}