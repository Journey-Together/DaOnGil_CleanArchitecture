package kr.techit.lion.presentation.ext

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import kr.techit.lion.presentation.R

fun Context.setImageSmall(imageView: ImageView, url: String?) {

    Glide.with(this).load(url)
        .placeholder(R.drawable.empty_view_small) // 로딩 중일 때
        .error(R.drawable.empty_view_small) // 오류 발생 시
        .into(imageView)

}

fun Context.setImage(imageView: ImageView, url: String?) {

    Glide.with(this).load(url)
        .placeholder(R.drawable.empty_view) // 로딩 중일 때
        .error(R.drawable.empty_view) // 오류 발생 시
        .into(imageView)

}