package kr.techit.lion.presentation.connectivity

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ConnectivityModule {
    @Binds
    fun bindConnectivityObserver(connectivity: NetworkConnectivityObserver): ConnectivityObserver
}
