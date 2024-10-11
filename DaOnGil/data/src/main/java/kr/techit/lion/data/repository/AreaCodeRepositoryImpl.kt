package kr.techit.lion.data.repository

import kr.techit.lion.data.database.entity.toDomainModel
import kr.techit.lion.data.database.entity.toEntity
import kr.techit.lion.data.datasource.AreaCodeDataSource
import kr.techit.lion.data.mapper.toFullAreaName
import kr.techit.lion.domain.model.area.AreaCode
import kr.techit.lion.domain.model.area.AreaCodeList
import kr.techit.lion.domain.repository.AreaCodeRepository
import javax.inject.Inject

internal class AreaCodeRepositoryImpl @Inject constructor(
    private val areaCodeDataSource: AreaCodeDataSource,
) : AreaCodeRepository {

    // 이름으로 지역코드 검색
    override fun getAreaCodeByName(areaName: String): String? {
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