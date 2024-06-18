package com.leeweeder.weighttracker.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weight_tracker")

class DataStoreRepositoryImpl(context: Context): DataStoreRepository {
    private object PreferencesKey {
        val goalWeightKey = doublePreferencesKey(name = "GOAL_WEIGHT")
        val shouldHideOnBoardingKey = booleanPreferencesKey(name = "ONBOARDING")
    }

    private val dataStore = context.dataStore

    override suspend fun saveGoalWeight(value: Double) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.goalWeightKey] = value
        }
    }

    override fun readGoalWeightState(): Flow<Double> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { preferences ->
                val goalWeightState = preferences[PreferencesKey.goalWeightKey] ?: 0.0
                goalWeightState
            }
    }

    override suspend fun saveOnBoardingState(shouldHideOnBoarding: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.shouldHideOnBoardingKey] = shouldHideOnBoarding
        }
    }

    override fun readOnBoardingState(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is java.io.IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { preferences ->
                val onBoardingState = preferences[PreferencesKey.shouldHideOnBoardingKey] ?: false
                onBoardingState
            }
    }
}