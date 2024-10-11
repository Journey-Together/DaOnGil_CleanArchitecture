package kr.techit.lion.domain.model.schedule

import java.net.URI

data class ReviewImage(
    val imageUrl: String? = null,
    val imageUri: URI,
    // 갤러리에서 선택한 이미지의 file Path
    val imagePath: String? = null
)
