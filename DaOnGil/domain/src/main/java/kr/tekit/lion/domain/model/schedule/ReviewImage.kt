package kr.tekit.lion.domain.model.schedule

import com.sun.jndi.toolkit.url.Uri

data class ReviewImage(
    val imageUrl: String? = null,
    val imageUri: Uri,
    // 갤러리에서 선택한 이미지의 file Path
    val imagePath: String? = null
)
