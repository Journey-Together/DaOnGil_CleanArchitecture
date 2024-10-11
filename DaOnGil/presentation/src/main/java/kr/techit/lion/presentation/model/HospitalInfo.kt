package kr.techit.lion.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.techit.lion.domain.model.HospitalMapInfo

@Parcelize
data class HospitalInfo(
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
): Parcelable

fun HospitalMapInfo.toHospitalInfo(): HospitalInfo {
    return HospitalInfo(
        hospitalId = this.hospitalId,
        emergencyCount = this.emergencyCount,
        emergencyKid = this.emergencyKid,
        emergencyKidCount = this.emergencyKidCount,
        emergencyAllCount = this.emergencyAllCount,
        emergencyKidAllCount = this.emergencyKidAllCount,
        emergencyBed = this.emergencyBed,
        emergencyKidBed = this.emergencyKidBed,
        lastUpdateDate = this.lastUpdateDate,
        hospitalName = this.hospitalName,
        hospitalAddress = this.hospitalAddress,
        hospitalTel = this.hospitalTel,
        emergencyTel = this.emergencyTel,
        dialysis = this.dialysis,
        earlyBirth = this.earlyBirth,
        burns = this.burns,
        hospitalLon = this.hospitalLon,
        hospitalLat = this.hospitalLat
    )
}
