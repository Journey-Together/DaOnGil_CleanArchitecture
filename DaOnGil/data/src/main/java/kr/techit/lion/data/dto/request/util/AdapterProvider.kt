package kr.techit.lion.data.dto.request.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

internal class AdapterProvider {
    companion object {
        fun <T> JsonAdapter(requestModel: Class<T>): JsonAdapter<T> {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return moshi.adapter(requestModel)
        }
    }
}
