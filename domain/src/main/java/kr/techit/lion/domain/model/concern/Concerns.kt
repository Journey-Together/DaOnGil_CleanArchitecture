package kr.techit.lion.domain.model.concern

data class Concerns(
    private val values: Map<ConcernType, Boolean> = ConcernType.entries.associateWith { false },
) {
    operator fun get(type: ConcernType): Boolean = values[type] ?: false

    fun anyTrue(): Boolean = values.containsValue(true)

    fun selectedType(): Set<ConcernType> = values.filterValues { it }.keys

    fun toMap(): Map<ConcernType, Boolean> = values.toMap()

    fun update(type: ConcernType): Concerns {
        val currentConcerns = values.toMutableMap()
        val selected = currentConcerns[type] ?: false
        currentConcerns[type] = !selected

        return Concerns(currentConcerns)
    }
}
