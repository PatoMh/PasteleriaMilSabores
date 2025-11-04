package com.example.pasteleriamilsabores.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class AuthManager(private val context: Context) {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    suspend fun login() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = false
        }
    }
}
