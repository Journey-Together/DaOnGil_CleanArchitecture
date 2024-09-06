package kr.tekit.lion.presentation.home.model

import android.graphics.BitmapFactory
import kr.tekit.lion.domain.model.placereview.NewReviewImages
import kr.tekit.lion.presentation.ext.compressBitmap
import java.io.File

data class NewReviewImgs (
    val images: List<String>
) {
    fun toDomainModel(): NewReviewImages {
        val byteArrays = mutableListOf<ByteArray>()

        this.images.forEach { imgPath ->
            val file = File(imgPath)
            val bitmap = BitmapFactory.decodeFile(file.path)?.compressBitmap(60)
            bitmap?.let {
                byteArrays.add(it)
            }
        }

        return NewReviewImages(byteArrays.takeIf { it.isNotEmpty() })
    }
}
