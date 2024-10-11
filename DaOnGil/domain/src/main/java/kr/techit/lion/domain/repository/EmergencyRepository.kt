package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.EmergencyBasicInfo
import kr.techit.lion.domain.model.EmergencyMessageInfo
import kr.techit.lion.domain.model.EmergencyRealtimeInfo
import kr.techit.lion.domain.exception.Result

interface EmergencyRepository {
    suspend fun getEmergencyRealtime(stage1: String?, stage2: String?): List<EmergencyRealtimeInfo>

    suspend fun getEmergencyBasic(hpid: String?): EmergencyBasicInfo

    suspend fun getEmergencyMessage(hpid: String?): Result<List<EmergencyMessageInfo>>
}