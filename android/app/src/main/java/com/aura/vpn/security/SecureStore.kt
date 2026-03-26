package com.aura.vpn.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStore {
    private const val FILE_NAME = "aura_secure_store"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_CONFIG_TOKEN = "config_token"
    private const val KEY_CONFIG_EXPIRES_AT = "config_expires_at"

    private var prefs: SharedPreferences? = null

    // Debug fallback to avoid hard crash on problematic devices.
    private val fallback = mutableMapOf<String, String>()

    fun init(context: Context) {
        if (prefs != null) return

        prefs = runCatching {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }.getOrNull()
    }

    fun getAuthToken(): String? = prefs?.getString(KEY_AUTH_TOKEN, null) ?: fallback[KEY_AUTH_TOKEN]

    fun setAuthToken(token: String) {
        prefs?.edit()?.putString(KEY_AUTH_TOKEN, token)?.apply() ?: run {
            fallback[KEY_AUTH_TOKEN] = token
        }
    }

    fun saveConfigToken(token: String?, expiresAt: String?) {
        prefs?.edit()?.putString(KEY_CONFIG_TOKEN, token)?.putString(KEY_CONFIG_EXPIRES_AT, expiresAt)?.apply() ?: run {
            if (token != null) fallback[KEY_CONFIG_TOKEN] = token
            if (expiresAt != null) fallback[KEY_CONFIG_EXPIRES_AT] = expiresAt
        }
    }
}
