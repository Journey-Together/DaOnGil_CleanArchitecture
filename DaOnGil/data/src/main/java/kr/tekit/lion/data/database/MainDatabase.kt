package kr.tekit.lion.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.tekit.lion.daongil.data.dto.local.AreaCodeEntity
import kr.tekit.lion.data.database.entity.SigunguCodeEntity
import kr.tekit.lion.data.database.dao.AreaCodeDao
import kr.tekit.lion.data.database.dao.SigunguCodeDao

@TypeConverters(ListConverter::class)
@Database(
    entities = [AreaCodeEntity::class, SigunguCodeEntity::class],
    version = 1
)
abstract class MainDatabase: RoomDatabase()  {
    abstract fun areaCodeDao(): AreaCodeDao
    abstract fun sigunguCodeDao(): SigunguCodeDao
}