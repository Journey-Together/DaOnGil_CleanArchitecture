package kr.techit.lion.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.techit.lion.domain.model.PharmacyMapInfo

@Parcelize
data class PharmacyInfo(
    val pharmacyAddress: String?,
    val pharmacyName: String?,
    val pharmacyTel: String?,
    val pharmacyLocation: String?,
    val monTime: String?,
    val tueTime: String?,
    val wedTime: String?,
    val thuTime: String?,
    val friTime: String?,
    val satTime: String?,
    val sunTime: String?,
    val holTime: String?,
    val pharmacyLat: Double?,
    val pharmacyLon: Double?
): Parcelable

fun PharmacyMapInfo.toPharmacyInfo(): PharmacyInfo {
    return PharmacyInfo(
        pharmacyAddress = this.pharmacyAddress,
        pharmacyName = this.pharmacyName,
        pharmacyTel = this.pharmacyTel,
        pharmacyLocation = this.pharmacyLocation,
        monTime = this.monTime,
        tueTime = this.tueTime,
        wedTime = this.wedTime,
        thuTime = this.thuTime,
        friTime = this.friTime,
        satTime = this.satTime,
        sunTime = this.sunTime,
        holTime = this.holTime,
        pharmacyLat = this.pharmacyLat,
        pharmacyLon = this.pharmacyLon
    )
}