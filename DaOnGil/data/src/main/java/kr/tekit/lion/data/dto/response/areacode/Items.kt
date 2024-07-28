package kr.tekit.lion.data.dto.response.areacode

import com.squareup.moshi.JsonClass
import kr.tekit.lion.data.dto.response.areacode.Item

@JsonClass(generateAdapter = true)
internal data class Items(
    val item: List<Item>
)