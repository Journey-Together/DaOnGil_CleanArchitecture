package kr.techit.lion.data.datasource

import kr.techit.lion.data.service.EmergencyService
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.data.common.execute
import kr.techit.lion.domain.model.EmergencyBasicInfo
import kr.techit.lion.domain.model.EmergencyMessageInfo
import kr.techit.lion.domain.model.EmergencyRealtimeInfo
import javax.inject.Inject

internal class EmergencyDataSource @Inject constructor(
    private val emergencyService: EmergencyService
) {
    suspend fun getEmergencyRealtime(stage1: String?, stage2: String?) : List<EmergencyRealtimeInfo> {
        return emergencyService.getEmergencyRealtime(stage1, stage2).toDomainModel()
    }

    suspend fun getEmergencyBasic(hpid: String?) : EmergencyBasicInfo {
        return emergencyService.getEmergencyBasic(hpid).toDomainModel()
    }

    suspend fun getEmergencyMessage(hpid: String?): Result<List<EmergencyMessageInfo>> = execute {
        emergencyService.getEmergencyMessage(hpid).toDomainModel()
    }
}