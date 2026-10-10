package com.aurafit.app.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPrefs(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "aurafit_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveOpenAiKey(key: String) {
        sharedPreferences.edit().putString(KEY_OPENAI, key).apply()
    }

    fun getOpenAiKey(): String {
        return sharedPreferences.getString(KEY_OPENAI, "") ?: ""
    }

    fun saveSupabaseConfig(url: String, key: String) {
        sharedPreferences.edit()
            .putString(KEY_SUPABASE_URL, url)
            .putString(KEY_SUPABASE_KEY, key)
            .apply()
    }

    fun getSupabaseUrl(): String = sharedPreferences.getString(KEY_SUPABASE_URL, "") ?: ""
    fun getSupabaseKey(): String = sharedPreferences.getString(KEY_SUPABASE_KEY, "") ?: ""

    fun saveOfflineMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_OFFLINE_MODE, enabled).apply()
    }

    fun isOfflineMode(): Boolean = sharedPreferences.getBoolean(KEY_OFFLINE_MODE, false)

    companion object {
        private const val KEY_OPENAI = "openai_api_key"
        private const val KEY_SUPABASE_URL = "supabase_project_url"
        private const val KEY_SUPABASE_KEY = "supabase_anon_key"
        private const val KEY_OFFLINE_MODE = "local_offline_only"
    }
}
