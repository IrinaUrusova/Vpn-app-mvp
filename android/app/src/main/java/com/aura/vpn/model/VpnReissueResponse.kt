package com.aura.vpn.model

data class VpnReissueResponse(
    val status: String,
    val event: String,
    val user_id: String,
    val old_uuid: String?,
    val new_uuid: String,
    val new_config: String,
    val reissue_count_today: Int,
    val ts: String,
)
