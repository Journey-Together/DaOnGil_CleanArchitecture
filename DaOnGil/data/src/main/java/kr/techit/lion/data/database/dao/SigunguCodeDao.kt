package kr.techit.lion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.techit.lion.data.database.entity.SigunguCodeEntity

@Dao
internal interface SigunguCodeDao {

    @Query("SELECT sigunguCode FROM SIGUNGU_CODE_TABLE WHERE (sigunguName = :villageName) AND (areaCode = :areaCode)")
    fun getSigunguCodeByVillageName(villageName: String, areaCode: String): String?

    @Query("SELECT * FROM SIGUNGU_CODE_TABLE WHERE areaCode = :code")
    suspend fun getSigunguCode(code: String): List<SigunguCodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setVillageCode(villageCodes: List<SigunguCodeEntity>)

}