package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.EmergencyBasicInfo
import kr.tekit.lion.domain.model.EmergencyMessageInfo
import kr.tekit.lion.domain.model.EmergencyRealtimeInfo
import kr.tekit.lion.domain.exception.Result

interface EmergencyRepository {
    suspend fun getEmergencyRealtime(stage1: String?, stage2: String?): List<EmergencyRealtimeInfo>

    suspend fun getEmergencyBasic(hpid: String?): EmergencyBasicInfo

    suspend fun getEmergencyMessage(hpid: String?): Result<List<EmergencyMessageInfo>>
}