package kr.tekit.lion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.tekit.lion.domain.model.SigunguCode

@Entity(tableName = "sigungu_code_table")
data class SigunguCodeEntity (
    @PrimaryKey
    val sigunguName: String,
    val sigunguCode: String,
    val areaCode: String
)

fun SigunguCodeEntity.toDomainModel() =
    SigunguCode(
        sigunguName = sigunguName,
        sigunguCode = sigunguCode,
        areaCode = areaCode
    )

fun SigunguCode.toEntity(): SigunguCodeEntity =
    SigunguCodeEntity(
        sigunguName = sigunguName,
        sigunguCode = sigunguCode,
        areaCode = areaCode
    )




