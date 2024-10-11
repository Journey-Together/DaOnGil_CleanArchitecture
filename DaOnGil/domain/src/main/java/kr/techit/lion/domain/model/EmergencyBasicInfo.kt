package kr.techit.lion.domain.model

data class EmergencyBasicInfo(
    val hospitalId : String?,
    val hospitalName: String?,
    val hospitalAddress: String?,
    val hospitalTel: String?,
    val emergencyTel: String?,
    val dialysis: String?,
    val earlyBirth: String?,
    val burns: String?,
    val hospitalLon: Double?,
    val hospitalLat: Double?
)
