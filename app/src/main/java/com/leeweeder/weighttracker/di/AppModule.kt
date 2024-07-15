package com.leeweeder.weighttracker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.leeweeder.weighttracker.data.datasource.AppDatabase
import com.leeweeder.weighttracker.data.repository.DataStoreRepositoryImpl
import com.leeweeder.weighttracker.data.repository.LogRepositoryImpl
import com.leeweeder.weighttracker.domain.repository.DataStoreRepository
import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.domain.usecases.DataStoreUseCases
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.leeweeder.weighttracker.domain.usecases.datastore.ReadGoalWeightState
import com.leeweeder.weighttracker.domain.usecases.datastore.ReadOnBoardingState
import com.leeweeder.weighttracker.domain.usecases.datastore.SaveGoalWeight
import com.leeweeder.weighttracker.domain.usecases.datastore.SaveShouldHideOnBoarding
import com.leeweeder.weighttracker.domain.usecases.log.DeleteLogById
import com.leeweeder.weighttracker.domain.usecases.log.GetLogByDate
import com.leeweeder.weighttracker.domain.usecases.log.GetLogById
import com.leeweeder.weighttracker.domain.usecases.log.GetLogs
import com.leeweeder.weighttracker.domain.usecases.log.GetLogsForThisWeek
import com.leeweeder.weighttracker.domain.usecases.log.InsertLog
import com.leeweeder.weighttracker.domain.usecases.log.UpdateLog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesMyApplicationDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideLogRepository(db: AppDatabase): LogRepository {
        return LogRepositoryImpl(db.logDao)
    }

    @Provides
    @Singleton
    fun provideLogUseCases(repository: LogRepository): LogUseCases {
        return LogUseCases(
            getLogs = GetLogs(repository),
            getLogById = GetLogById(repository),
            insertLog = InsertLog(repository),
            updateLog = UpdateLog(repository),
            deleteLogById = DeleteLogById(repository),
            getLogsForThisWeek = GetLogsForThisWeek(repository),
            getLogByDate = GetLogByDate(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ): DataStoreRepository = DataStoreRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideDataStoreUseCases(repository: DataStoreRepository): DataStoreUseCases {
        return DataStoreUseCases(
            saveGoalWeight = SaveGoalWeight(repository),
            readGoalWeightState = ReadGoalWeightState(repository),
            saveShouldHideOnBoarding = SaveShouldHideOnBoarding(repository),
            readOnBoardingState = ReadOnBoardingState(repository)
        )
    }
}