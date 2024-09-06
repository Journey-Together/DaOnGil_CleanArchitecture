package kr.tekit.lion.domain.model

data class EmergencyMapInfo(
    val hospitalList: HospitalMapInfo?,
    val emergencyType: String,
    val emergencyId: String?,
    val aedList: AedMapInfo?
)
