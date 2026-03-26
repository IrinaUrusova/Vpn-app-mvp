package com.aura.vpn.network

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    fun create(tokenProvider: () -> String): AuraApi {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val authToken = tokenProvider().trim()
                val req: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(req)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api-aura.kupisait1.ru")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuraApi::class.java)
    }
}
