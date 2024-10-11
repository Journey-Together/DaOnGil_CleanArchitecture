package kr.techit.lion.domain.usecase.areacode

import kr.techit.lion.domain.model.area.SigunguCode
import kr.techit.lion.domain.repository.AreaCodeRepository
import kr.techit.lion.domain.repository.KorWithRepository
import kr.techit.lion.domain.repository.SigunguCodeRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import javax.inject.Inject

class InitAreaCodeInfoUseCase @Inject constructor(
    private val korWithRepository: KorWithRepository,
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
): BaseUseCase() {

    suspend operator fun invoke(): Result<Unit> = execute {
        val areaCodes = korWithRepository.getAreaCodeInfo()

        areaCodeRepository.addAreaCodeInfo(areaCodes)

        val sigunguCodes = areaCodes.map {
            korWithRepository.getSigunguCode(it.code)
        }

        val sigunguCodeList = sigunguCodes.mapIndexed { idx, sigungu ->
            sigungu.map {
                SigunguCode(
                    areaCode = areaCodes[idx].code,
                    sigunguCode = it.code,
                    sigunguName = it.name
                )
            }
        }

        sigunguCodeList.map { sigunguCodeRepository.addSigunguCode(it) }
    }
}
