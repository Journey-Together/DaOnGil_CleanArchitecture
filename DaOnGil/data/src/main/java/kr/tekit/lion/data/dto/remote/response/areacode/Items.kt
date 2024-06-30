package kr.tekit.lion.data.dto.remote.response.areacode

import com.squareup.moshi.JsonClass
import kr.tekit.lion.data.dto.remote.response.areacode.Item

@JsonClass(generateAdapter = true)
data class Items(
    val item: List<Item>
)