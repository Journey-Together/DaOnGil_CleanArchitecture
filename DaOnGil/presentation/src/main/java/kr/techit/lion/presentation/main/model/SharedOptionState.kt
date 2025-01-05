package kr.techit.lion.presentation.main.model

import java.util.TreeSet

data class SharedOptionState(
    val disabilityType: TreeSet<Long> = TreeSet(),
    val detailFilter: TreeSet<Long> = TreeSet()
){
    fun clear(): SharedOptionState {
        return this.copy(
            disabilityType = TreeSet(),
            detailFilter = TreeSet()
        )
    }
}