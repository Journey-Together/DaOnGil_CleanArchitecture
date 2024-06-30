package kr.tekit.lion.daongil.data.dto.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.tekit.lion.domain.model.AreaCode

@Entity(tableName = "area_code_table")
data class AreaCodeEntity (
    @PrimaryKey
    val code: String,
    val name: String
)

fun AreaCodeEntity.toDomainModel(): AreaCode =
    AreaCode(
        code = code,
        name = name
    )

fun AreaCode.toEntity(): AreaCodeEntity =
    AreaCodeEntity(
        code = code,
        name = name
    )

