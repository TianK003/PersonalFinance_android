package mau.se.personalfinance.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_SURNAME = stringPreferencesKey("user_surname")
    }

    val userName: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    val userSurname: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_SURNAME] ?: ""
        }

    suspend fun saveUserDetails(name: String, surname: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_SURNAME] = surname
        }
    }
}