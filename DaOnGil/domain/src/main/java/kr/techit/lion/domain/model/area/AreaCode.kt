package kr.techit.lion.domain.model.area

data class AreaCode (
    val code: String,
    val name: String
)

data class AreaCodeList(
    val areaList: List<AreaCode>
){
    fun getAllAreaName(): List<String>{
        return areaList.map { it.name }
    }

    fun findAreaCode(areaName: String): String{
        return areaList.find { it.name == areaName }?.code ?: ""
    }
}