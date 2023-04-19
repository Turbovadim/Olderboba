package net.turbovadim.bisquithost.DataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StoreApiKey (val context: Context) {
    companion object {
        private val Context.dataStoree: DataStore<Preferences> by preferencesDataStore("user_api")
        val USER_API_KEY = stringPreferencesKey("user_api")
    }

    suspend fun readApiKey(): String? {
        val preferences = context.dataStoree.data.first()
        return preferences[USER_API_KEY] ?: ""
    }

    suspend fun saveApiKey(key: String) {
        context.dataStoree.edit { preferences ->
            preferences[USER_API_KEY] = key
        }
    }
}