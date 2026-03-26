package com.aura.vpn.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.vpn.network.ApiFactory
import com.aura.vpn.security.SecureStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = false,
    val apiStatus: String = "idle",
    val configPreview: String = "Конфиг ещё не загружен",
)

class HomeViewModel : ViewModel() {
    private var authToken: String = "dev-aura-token"
    private val api get() = ApiFactory.create { authToken }

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun bootstrapAuth(token: String) {
        authToken = token
        SecureStore.setAuthToken(token)
    }

    fun fetchConfig() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, apiStatus = "loading")
            runCatching { api.getConfig() }
                .onSuccess {
                    SecureStore.saveConfigToken(it.config_token, it.expires_at)
                    _state.value = _state.value.copy(
                        loading = false,
                        apiStatus = "ok",
                        configPreview = it.config_preview ?: (it.config?.take(120)?.plus("...") ?: "Config unavailable"),
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        loading = false,
                        apiStatus = "error: ${it.message}",
                    )
                }
        }
    }

    fun reissue() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, apiStatus = "reissue...")
            runCatching { api.reissue() }
                .onSuccess {
                    _state.value = _state.value.copy(
                        loading = false,
                        apiStatus = "reissued #${it.reissue_count_today}",
                        configPreview = it.new_config.take(120) + "...",
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        loading = false,
                        apiStatus = "reissue error: ${it.message}",
                    )
                }
        }
    }
}
