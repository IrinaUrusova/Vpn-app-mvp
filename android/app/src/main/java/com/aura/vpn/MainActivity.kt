package com.aura.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aura.vpn.security.SecureStore
import com.aura.vpn.ui.HomeScreen
import com.aura.vpn.ui.HomeViewModel

class MainActivity : ComponentActivity() {
    private val vm: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SecureStore.init(this)
        vm.bootstrapAuth(SecureStore.getAuthToken() ?: "dev-aura-token")

        setContent {
            val state by vm.state.collectAsState()

            HomeScreen(
                apiStatus = state.apiStatus,
                configPreview = state.configPreview,
                loading = state.loading,
                onConnectToggle = {
                    // VPN core wiring will be connected on next step
                },
                onFetchConfig = { vm.fetchConfig() },
                onReissue = { vm.reissue() },
            )
        }
    }
}
