package kr.techit.lion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.techit.lion.domain.model.area.SigunguCode

@Entity(tableName = "sigungu_code_table")
internal data class SigunguCodeEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sigunguName: String,
    val sigunguCode: String,
    val areaCode: String
)

internal fun SigunguCodeEntity.toDomainModel() =
    SigunguCode(
        sigunguName = sigunguName,
        sigunguCode = sigunguCode,
        areaCode = areaCode
    )

internal fun SigunguCode.toEntity(): SigunguCodeEntity =
    SigunguCodeEntity(
        sigunguName = sigunguName,
        sigunguCode = sigunguCode,
        areaCode = areaCode
    )




