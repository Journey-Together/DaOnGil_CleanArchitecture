package kr.tekit.lion.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kr.tekit.lion.data.database.entity.AreaCodeEntity
import kr.tekit.lion.data.database.entity.SigunguCodeEntity
import kr.tekit.lion.data.database.dao.AreaCodeDao
import kr.tekit.lion.data.database.dao.RecentlySearchKeywordDAO
import kr.tekit.lion.data.database.dao.SigunguCodeDao
import kr.tekit.lion.data.database.entity.RecentlySearchKeywordEntity

@TypeConverters(ListConverter::class)
@Database(
    entities = [AreaCodeEntity::class, SigunguCodeEntity::class, RecentlySearchKeywordEntity::class],
    version = 3
)
internal abstract class MainDatabase: RoomDatabase()  {
    abstract fun areaCodeDao(): AreaCodeDao
    abstract fun sigunguCodeDao(): SigunguCodeDao
    abstract fun recentlySearchKeywordDao() : RecentlySearchKeywordDAO
}