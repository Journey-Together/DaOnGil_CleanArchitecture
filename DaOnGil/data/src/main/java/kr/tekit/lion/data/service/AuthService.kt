package kr.tekit.lion.data.service

import kr.tekit.lion.data.dto.response.SignUpResponse
import kr.tekit.lion.data.dto.response.signin.SignInResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Tag

internal interface AuthService {
    @POST("auth/sign-in")
    suspend fun signIn(
        @Query("type") type: String,
        @Header("Authorization") token: String,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): SignInResponse

    @POST("auth/sign-in")
    suspend fun login(
        @Query("type") type: String,
        @Tag authType: AuthType = AuthType.ACCESS_TOKEN
    ): SignInResponse

    @POST("auth/reissue")
    suspend fun refresh(
        @Header("Authorization") refreshToken: String,
    ): SignUpResponse
}