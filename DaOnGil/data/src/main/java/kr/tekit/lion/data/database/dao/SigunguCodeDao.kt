package kr.tekit.lion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.tekit.lion.data.database.entity.SigunguCodeEntity

@Dao
internal interface SigunguCodeDao {

    @Query("SELECT sigunguCode FROM SIGUNGU_CODE_TABLE WHERE sigunguName LIKE :villageName || '%' ")
    suspend fun getSigunguCodeByVillageName(villageName: String): String?

    @Query("SELECT * FROM SIGUNGU_CODE_TABLE WHERE areaCode = :code")
    suspend fun getSigunguCode(code: String): List<SigunguCodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setVillageCode(villageCodes: List<SigunguCodeEntity>)

}