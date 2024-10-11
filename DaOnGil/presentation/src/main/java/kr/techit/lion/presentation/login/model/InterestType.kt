package kr.techit.lion.presentation.login.model

sealed class InterestType {
    data object Physical: InterestType()
    data object Hear : InterestType()
    data object Visual : InterestType()
    data object Elderly : InterestType()
    data object Child : InterestType()
}