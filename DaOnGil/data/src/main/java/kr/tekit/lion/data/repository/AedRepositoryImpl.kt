package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.AedDataSource
import kr.tekit.lion.domain.model.AedMapInfo
import kr.tekit.lion.domain.repository.AedRepository
import kr.tekit.lion.domain.exception.Result
import javax.inject.Inject

internal class AedRepositoryImpl @Inject constructor(
    private val aedDataSource: AedDataSource
): AedRepository {
    override suspend fun getAedInfo(q0: String?, q1: String?): Result<List<AedMapInfo>> {
        return aedDataSource.getAedInfo(q0, q1)
    }

}