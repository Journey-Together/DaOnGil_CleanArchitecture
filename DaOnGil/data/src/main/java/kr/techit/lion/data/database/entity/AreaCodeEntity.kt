package kr.techit.lion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.techit.lion.domain.model.area.AreaCode

@Entity(tableName = "area_code_table")
internal data class AreaCodeEntity (
    @PrimaryKey
    val code: String,
    val name: String
)

internal fun AreaCodeEntity.toDomainModel(): AreaCode =
    AreaCode(
        code = code,
        name = name
    )

internal fun AreaCode.toEntity(): AreaCodeEntity =
    AreaCodeEntity(
        code = code,
        name = name
    )

