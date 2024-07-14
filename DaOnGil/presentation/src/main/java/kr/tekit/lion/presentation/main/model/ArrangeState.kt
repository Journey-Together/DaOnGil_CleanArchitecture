package kr.tekit.lion.presentation.main.model

sealed class ArrangeState(val sortCode: String)
data object SortByLatest: ArrangeState("A")
data object SortByPopularity: ArrangeState("B")
data object SortByLetter: ArrangeState("C")
