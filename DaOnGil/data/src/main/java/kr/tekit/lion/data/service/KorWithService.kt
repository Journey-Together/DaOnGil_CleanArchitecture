package kr.tekit.lion.data.service

import kr.tekit.lion.data.dto.remote.response.areacode.AreaCodeResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface KorWithService {
    @GET("areaCode1")
    suspend fun getAreaCode(
        @QueryMap params: Map<String, String>
    ): AreaCodeResponse

}