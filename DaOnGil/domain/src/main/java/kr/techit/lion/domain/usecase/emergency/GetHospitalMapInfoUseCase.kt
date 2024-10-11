package kr.techit.lion.domain.usecase.emergency

import kr.techit.lion.domain.model.EmergencyBasicInfo
import kr.techit.lion.domain.model.EmergencyRealtimeInfo
import kr.techit.lion.domain.model.HospitalMapInfo
import kr.techit.lion.domain.repository.EmergencyRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import javax.inject.Inject

class GetHospitalMapInfoUseCase @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) : BaseUseCase() {

    suspend operator fun invoke(stage1: String?, stage2: String?): Result<List<HospitalMapInfo>> =
        execute {
            val emergencyRealtimeList = emergencyRepository.getEmergencyRealtime(stage1, stage2)
            val emergencyMapInfoList = emergencyRealtimeList.map {
                val emergencyBasicInfo = emergencyRepository.getEmergencyBasic(it.hospitalId)
                setEmergencyMapInfo(it, emergencyBasicInfo)
            }
            emergencyMapInfoList
        }

    private fun setEmergencyMapInfo(
        realtimeInfo: EmergencyRealtimeInfo,
        basicInfo: EmergencyBasicInfo
    ): HospitalMapInfo{
        return HospitalMapInfo(
            hospitalId = realtimeInfo.hospitalId,
            emergencyCount = realtimeInfo.emergencyCount,
            emergencyKid = realtimeInfo.emergencyKid,
            emergencyKidCount = realtimeInfo.emergencyKidCount,
            emergencyAllCount = realtimeInfo.emergencyAllCount,
            emergencyKidAllCount = realtimeInfo.emergencyKidAllCount,
            emergencyBed = realtimeInfo.emergencyCount?.let { count ->
                realtimeInfo.emergencyAllCount?.let { allCount ->
                    ((count.toFloat() / allCount) * 100).toInt()
                }
            },
            emergencyKidBed =  realtimeInfo.emergencyKidCount?.let { kidCount ->
                realtimeInfo.emergencyKidAllCount?.let { kidAllCount ->
                    ((kidCount.toFloat() / kidAllCount) * 100).toInt()
                }
            },
            lastUpdateDate = realtimeInfo.lastUpdateDate,
            hospitalName = basicInfo.hospitalName,
            hospitalAddress = basicInfo.hospitalAddress,
            hospitalTel = basicInfo.hospitalTel,
            emergencyTel = basicInfo.emergencyTel,
            dialysis = basicInfo.dialysis,
            earlyBirth = basicInfo.earlyBirth,
            burns = basicInfo.burns,
            hospitalLon = basicInfo.hospitalLon,
            hospitalLat = basicInfo.hospitalLat
        )
    }
}