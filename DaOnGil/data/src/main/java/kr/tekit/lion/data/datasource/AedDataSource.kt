package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.common.execute
import kr.tekit.lion.data.service.AedService
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.data.common.execute
import kr.tekit.lion.domain.model.AedMapInfo
import javax.inject.Inject

internal class AedDataSource @Inject constructor(
    private val aedService: AedService
) {
    suspend fun getAedInfo(q0: String?, q1: String?) : List<AedMapInfo> {
        return aedService.getAedInfo(q0, q1).toDomainModel()
    }
}