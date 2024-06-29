package com.leeweeder.weighttracker.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.leeweeder.weighttracker.StartingWeight
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import java.time.format.DateTimeFormatter

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weight_tracker")
val Context.startingWeightDataStore: DataStore<StartingWeight> by dataStore(
    fileName = "starting_weight.pb",
    serializer = StartingWeightSerializer
)

class DataStoreRepositoryImpl(context: Context) : DataStoreRepository {
    private object PreferencesKey {
        val goalWeightKey = intPreferencesKey(name = "GOAL_WEIGHT")
        val shouldHideOnBoardingKey = booleanPreferencesKey(name = "ONBOARDING")
    }

    private val dataStore = context.dataStore
    private val startingWeightDataStore = context.startingWeightDataStore
    override val startingWeightFlow: Flow<StartingWeight> = startingWeightDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                android.util.Log.e("ProtoDataStore", "Error reading starting weight: $exception")
                emit(StartingWeight.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override suspend fun saveGoalWeight(value: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.goalWeightKey] = value
        }
    }

    override fun readGoalWeightState(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }
            .map { preferences ->
                val goalWeightState = preferences[PreferencesKey.goalWeightKey] ?: 0
                goalWeightState
            }
    }

    override suspend fun saveShouldHideOnBoarding(shouldHideOnBoarding: Boolean) {
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

    override suspend fun saveStartingWeight(log: Log) {
        startingWeightDataStore.updateData { preferences ->
            preferences.toBuilder().setWeight(log.weight.value)
                .setDate(log.date.format(DateTimeFormatter.ISO_LOCAL_DATE)).build()
        }
    }
}

object StartingWeightSerializer : Serializer<StartingWeight> {
    override val defaultValue: StartingWeight
        get() = StartingWeight.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StartingWeight {
        return try {
            StartingWeight.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            exception.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: StartingWeight, output: OutputStream) {
        t.writeTo(output)
    }

}