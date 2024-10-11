package kr.techit.lion.presentation.myinfo.model

import android.graphics.BitmapFactory
import kr.techit.lion.domain.model.ProfileImage
import kr.techit.lion.presentation.ext.compressBitmap
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