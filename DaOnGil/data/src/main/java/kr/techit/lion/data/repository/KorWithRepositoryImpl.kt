package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.KorWithDataSource
import kr.techit.lion.domain.model.area.AreaCode
import kr.techit.lion.domain.repository.KorWithRepository
import javax.inject.Inject

internal class KorWithRepositoryImpl @Inject constructor(
    private val korWithDataSource: KorWithDataSource
): KorWithRepository {

    // API 모든 지역코드 검색
    override suspend fun getAreaCodeInfo(): List<AreaCode> {
        return korWithDataSource.getAreaInfoList().toDomainModel()
    }

    // API 모든 시군구 코드 검색
    override suspend fun getSigunguCode(areaCode: String): List<AreaCode> {
        return korWithDataSource.getAreaInfoList(areaCode).toDomainModel()
    }
}