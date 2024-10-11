package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.AedDataSource
import kr.techit.lion.domain.model.AedMapInfo
import kr.techit.lion.domain.repository.AedRepository
import javax.inject.Inject

internal class AedRepositoryImpl @Inject constructor(
    private val aedDataSource: AedDataSource
): AedRepository {
    override suspend fun getAedInfo(q0: String?, q1: String?): List<AedMapInfo> {
        return aedDataSource.getAedInfo(q0, q1)
    }

}