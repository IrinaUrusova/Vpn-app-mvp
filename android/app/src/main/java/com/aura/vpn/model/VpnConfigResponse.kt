package com.aura.vpn.model

data class VpnConfigResponse(
    val status: String,
    val user_id: String,
    val config: String? = null,
    val config_token: String? = null,
    val expires_at: String? = null,
    val config_preview: String? = null,
)
