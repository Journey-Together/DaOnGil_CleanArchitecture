package kr.techit.lion.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.techit.lion.domain.model.AedMapInfo

@Parcelize
data class AedInfo (
    val aedName: String?,
    val aedAdress: String?,
    val managerTel: String?,
    val aedTel: String?,
    val aedLocation: String?,
    val monTime: String?,
    val tueTime: String?,
    val wedTime: String?,
    val thuTime: String?,
    val friTime: String?,
    val satTime: String?,
    val sunTime: String?,
    val holTime: String?,
    val sunAvailable: String?,
    val aedLon: Double?,
    val aedLat: Double?
): Parcelable

fun AedMapInfo.toAedInfo(): AedInfo {
    return AedInfo(
        aedName = this.aedName,
        aedAdress = this.aedAdress,
        managerTel = this.managerTel,
        aedTel = this.aedTel,
        aedLocation = this.aedLocation,
        monTime = this.monTime,
        tueTime = this.tueTime,
        wedTime = this.wedTime,
        thuTime = this.thuTime,
        friTime = this.friTime,
        satTime = this.satTime,
        sunTime = this.sunTime,
        holTime = this.holTime,
        sunAvailable = this.sunAvailable,
        aedLon = this.aedLon,
        aedLat = this.aedLat
    )
}
