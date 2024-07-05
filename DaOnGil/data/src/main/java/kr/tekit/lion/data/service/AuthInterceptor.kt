package kr.tekit.lion.data.service

import kotlinx.coroutines.runBlocking
import kr.tekit.lion.data.datasource.TokenDataSource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val authType = request.tag(AuthType::class.java) ?: AuthType.ACCESS_TOKEN

        when(authType){
            AuthType.NO_AUTH -> {
                builder.addHeader("Content-Type", "application/json")
            }
            AuthType.ACCESS_TOKEN -> {
                val accessToken = runBlocking{ tokenDataSource.getAccessToken() }

                builder.addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $accessToken")
            }
        }
        return chain.proceed(builder.build())
    }
}

enum class AuthType{
    NO_AUTH,
    ACCESS_TOKEN
}