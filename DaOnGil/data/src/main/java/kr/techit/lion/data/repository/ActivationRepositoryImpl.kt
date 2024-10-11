package kr.techit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kr.techit.lion.data.datasource.ActivationDataSource
import kr.techit.lion.domain.repository.ActivationRepository
import javax.inject.Inject

internal class ActivationRepositoryImpl @Inject constructor(
    private val activationDataSource: ActivationDataSource,
): ActivationRepository {
    override val userActivation: Flow<Boolean>
        get() = activationDataSource.activation

    override suspend fun saveUserActivation(active: Boolean) {
        activationDataSource.saveUserActivation(active)
    }
}