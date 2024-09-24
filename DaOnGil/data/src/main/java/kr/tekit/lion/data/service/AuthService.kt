package kr.tekit.lion.data.service

import kr.tekit.lion.data.dto.response.SignUpResponse
import kr.tekit.lion.data.dto.response.signin.SignInResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Tag

internal interface AuthService {
    @POST("auth/sign-in")
    suspend fun signIn(
        @Query("type") type: String,
        @Header("Authorization") token: String,
        @Body requestBody: RequestBody,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): SignInResponse

    @POST("auth/sign-out")
    suspend fun signOut(
        @Header("Authorization") token: String,
        @Tag authType: AuthType = AuthType.ACCESS_TOKEN
    ): SignInResponse

    @GET("auth/reissue")
    suspend fun refresh(
        @Header("Authorization") refreshToken: String,
    ): SignUpResponse

    @GET("auth/withdrawal")
    suspend fun withdraw(
        @Tag authType: AuthType = AuthType.ACCESS_TOKEN
    )
}