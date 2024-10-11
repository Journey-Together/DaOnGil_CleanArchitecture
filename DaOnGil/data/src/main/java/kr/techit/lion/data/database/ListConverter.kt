package kr.techit.lion.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import kr.techit.lion.data.database.entity.AreaCodeEntity
import kr.techit.lion.data.database.entity.SigunguCodeEntity

internal class ListConverter {

    @TypeConverter
    fun areaCodeListToJson(value: List<AreaCodeEntity>): String{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToAreaCodeList(value: String): List<AreaCodeEntity> {
        return Gson().fromJson(value, Array<AreaCodeEntity>::class.java).toList()
    }

    @TypeConverter
    fun villageCodeListToJson(value: List<SigunguCodeEntity>): String{
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToVillageCodeList(value: String): List<SigunguCodeEntity> {
        return Gson().fromJson(value, Array<SigunguCodeEntity>::class.java).toList()
    }
}