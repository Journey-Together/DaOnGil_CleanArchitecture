package kr.tekit.lion.domain.model.area

data class AreaCode (
    val code: String,
    val name: String
)

data class AreaCodeList(
    val areaList: List<AreaCode>
){
    fun findAreaCode(areaName: String): String?{
        return areaList.find { it.name == areaName }?.code
    }
}