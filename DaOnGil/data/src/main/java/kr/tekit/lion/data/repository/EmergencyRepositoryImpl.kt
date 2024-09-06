package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.EmergencyDataSource
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.EmergencyBasicInfo
import kr.tekit.lion.domain.model.EmergencyMessageInfo
import kr.tekit.lion.domain.model.EmergencyRealtimeInfo
import kr.tekit.lion.domain.repository.EmergencyRepository
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