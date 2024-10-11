package kr.techit.lion.domain.model

data class EmergencyRealtimeInfo(
    val hospitalId: String?,
    val emergencyCount: Int?,
    val emergencyKid: String?,
    val emergencyKidCount: Int?,
    val emergencyAllCount: Int?,
    val emergencyKidAllCount: Int?,
    val lastUpdateDate: String?
)
