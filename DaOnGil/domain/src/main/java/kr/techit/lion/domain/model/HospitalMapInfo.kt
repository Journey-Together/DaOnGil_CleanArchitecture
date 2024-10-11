package kr.techit.lion.domain.model

data class HospitalMapInfo(
    val hospitalId: String?,
    val emergencyCount: Int?,
    val emergencyKid: String?,
    val emergencyKidCount: Int?,
    val emergencyAllCount: Int?,
    val emergencyKidAllCount: Int?,
    val emergencyBed: Int?,
    val emergencyKidBed: Int?,
    val lastUpdateDate: String?,
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
