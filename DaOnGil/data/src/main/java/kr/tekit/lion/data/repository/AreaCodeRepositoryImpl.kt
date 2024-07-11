package kr.tekit.lion.data.repository

import kr.tekit.lion.data.database.entity.toDomainModel
import kr.tekit.lion.data.database.entity.toEntity
import kr.tekit.lion.data.datasource.AreaCodeDataSource
import kr.tekit.lion.data.mapper.toFullAreaName
import kr.tekit.lion.domain.model.AreaCode
import kr.tekit.lion.domain.model.AreaCodeList
import kr.tekit.lion.domain.repository.AreaCodeRepository
import javax.inject.Inject

class AreaCodeRepositoryImpl @Inject constructor(
    private val areaCodeDataSource: AreaCodeDataSource,
) : AreaCodeRepository {

    // 이름으로 지역코드 검색
    override suspend fun getAreaCodeByName(areaName: String): String? {
        return areaCodeDataSource.getAreaCodeInfo(areaName)
    }

    // 로컬의 모든 지역코드
    override suspend fun getAllAreaCodes(): AreaCodeList {
        return AreaCodeList(
            areaCodeDataSource.getAllAreaCodes().map {
                it.toDomainModel()
            }
        )
    }

    override suspend fun addAreaCodeInfo(areaCodeList: List<AreaCode>) {
        areaCodeDataSource.addAreaCodeInfoList(areaCodeList.map {
                AreaCode(it.code, it.name.toFullAreaName()).toEntity()
            }
        )
    }
}