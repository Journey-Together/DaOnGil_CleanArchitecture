package kr.tekit.lion.presentation.model

import android.net.Uri

data class ReviewImage(
    val imageUrl: String? = null,
    val imageUri: Uri,
    // 갤러리에서 선택한 이미지의 file Path
    val imagePath: String? = null
)
