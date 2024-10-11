package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.EmergencyDataSource
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.EmergencyBasicInfo
import kr.techit.lion.domain.model.EmergencyMessageInfo
import kr.techit.lion.domain.model.EmergencyRealtimeInfo
import kr.techit.lion.domain.repository.EmergencyRepository
import javax.inject.Inject

internal class EmergencyRepositoryImpl @Inject constructor(
    private val emergencyDataSource: EmergencyDataSource
): EmergencyRepository {
    override suspend fun getEmergencyRealtime(
        stage1: String?,
        stage2: String?
    ): List<EmergencyRealtimeInfo> {
        return emergencyDataSource.getEmergencyRealtime(stage1, stage2)
    }

    override suspend fun getEmergencyBasic(hpid: String?): EmergencyBasicInfo {
        return emergencyDataSource.getEmergencyBasic(hpid)
    }

    override suspend fun getEmergencyMessage(hpid: String?): Result<List<EmergencyMessageInfo>> {
        return emergencyDataSource.getEmergencyMessage(hpid)
    }

}