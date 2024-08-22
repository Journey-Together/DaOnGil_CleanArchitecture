package kr.tekit.lion.presentation.myinfo.model

import android.graphics.BitmapFactory
import kr.tekit.lion.domain.model.ProfileImage
import kr.tekit.lion.presentation.ext.compressBitmap
import java.io.File

data class UserProfileImg (
    val imagePath: String
){
    fun toDomainModel(): ProfileImage {
        val file = File(this.imagePath)
        val byteArray = BitmapFactory.decodeFile(file.path).compressBitmap(60)
        return ProfileImage(byteArray)
    }
}