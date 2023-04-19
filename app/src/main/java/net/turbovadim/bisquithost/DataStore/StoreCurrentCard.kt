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
import kotlinx.coroutines.runBlocking

class StoreCurrentCard (val context: Context) {
    companion object {
        private val Context.dataStoree: DataStore<Preferences> by preferencesDataStore("current_card")
        val CURRENT_CARD = stringPreferencesKey("current_card")
    }

    suspend fun readCurrentCard(): String? {
        val preferences = context.dataStoree.data.first()
        return preferences[CURRENT_CARD] ?: "Wide"
    }

    suspend fun saveCurrentCard(key: String) {
        context.dataStoree.edit { preferences ->
            preferences[CURRENT_CARD] = key
        }
    }
}

fun getCurrentServerCard(context: Context): String? {
    val dataStore = StoreCurrentCard(context)
    var currentCard = ""
    runBlocking {
        currentCard = dataStore.readCurrentCard()!!
    }
    return currentCard
}