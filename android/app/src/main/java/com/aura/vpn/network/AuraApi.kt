package com.aura.vpn.network

import com.aura.vpn.model.VpnConfigResponse
import com.aura.vpn.model.VpnReissueResponse
import retrofit2.http.GET
import retrofit2.http.POST

interface AuraApi {
    @GET("/v1/vpn/config")
    suspend fun getConfig(): VpnConfigResponse

    @POST("/v1/vpn/reissue")
    suspend fun reissue(): VpnReissueResponse
}
