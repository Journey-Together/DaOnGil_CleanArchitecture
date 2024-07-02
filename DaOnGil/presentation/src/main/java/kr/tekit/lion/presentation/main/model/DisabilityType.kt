package kr.tekit.lion.presentation.main.model

sealed class DisabilityType(val type: Long) {
    data object PhysicalDisability : DisabilityType(1)
    data object VisualImpairment : DisabilityType(2)
    data object HearingImpairment : DisabilityType(3)
    data object InfantFamily : DisabilityType(4)
    data object ElderlyPeople : DisabilityType(5)
}