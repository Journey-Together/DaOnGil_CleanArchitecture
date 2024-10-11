package kr.techit.lion.data.datasource

import kr.techit.lion.data.service.AedService
import kr.techit.lion.domain.model.AedMapInfo
import javax.inject.Inject

internal class AedDataSource @Inject constructor(
    private val aedService: AedService
) {
    suspend fun getAedInfo(q0: String?, q1: String?) : List<AedMapInfo> {
        return aedService.getAedInfo(q0, q1).toDomainModel()
    }
}