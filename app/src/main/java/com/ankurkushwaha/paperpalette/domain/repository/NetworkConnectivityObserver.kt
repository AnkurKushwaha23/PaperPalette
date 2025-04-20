package com.ankurkushwaha.paperpalette.domain.repository

import com.ankurkushwaha.paperpalette.domain.model.NetworkStatus
import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectivityObserver {
    val networkStatus: StateFlow<NetworkStatus>
}