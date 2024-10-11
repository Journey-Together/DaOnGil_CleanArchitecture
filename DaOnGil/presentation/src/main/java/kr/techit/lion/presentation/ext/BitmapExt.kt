package kr.techit.lion.presentation.ext

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.compressBitmap(quality: Int): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}