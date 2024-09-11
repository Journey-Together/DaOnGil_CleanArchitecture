package kr.tekit.lion.presentation.observer

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun getFlow(): Flow<Status>

    enum class Status{
        Available, Unavailable, Losing, Lost
    }
}