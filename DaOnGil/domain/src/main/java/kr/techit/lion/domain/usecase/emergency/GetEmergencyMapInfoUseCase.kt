package kr.techit.lion.domain.usecase.emergency

import kr.techit.lion.domain.model.EmergencyMapInfo
import kr.techit.lion.domain.repository.AedRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import kr.techit.lion.domain.usecase.base.onSuccess
import javax.inject.Inject

class GetEmergencyMapInfoUseCase @Inject constructor(
    private val getHospitalMapInfoUseCase: GetHospitalMapInfoUseCase,
    private val aedRepository: AedRepository
) : BaseUseCase() {

    suspend operator fun invoke(area: String?, areaDetail: String?): Result<List<EmergencyMapInfo>> =
        execute {
            val aedArea = if (area == "전북특별자치도") "전라북도" else area
            val editAreaDetail = if (area == "세종특별자치시") null else areaDetail

            val hospitalList = getHospitalMapInfoUseCase(area, areaDetail)
            val aedList = aedRepository.getAedInfo(aedArea, editAreaDetail)

            val emergencyList = mutableListOf<EmergencyMapInfo>()

            hospitalList.onSuccess {
                it.map {
                    emergencyList.add(
                        EmergencyMapInfo(
                            hospitalList = it,
                            emergencyType = "hospital",
                            emergencyId = it.hospitalId,
                            aedList = null
                        )
                    )
                }
            }

            aedList.map {
                emergencyList.add(
                    EmergencyMapInfo(
                        hospitalList = null,
                        emergencyType = "aed",
                        emergencyId = null,
                        aedList = it
                    )
                )
            }

            emergencyList
        }
}