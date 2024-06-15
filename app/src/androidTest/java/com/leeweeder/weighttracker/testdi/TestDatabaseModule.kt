package com.leeweeder.weighttracker.testdi

import android.app.Application
import com.leeweeder.weighttracker.data.datasource.AppDatabase
import com.leeweeder.weighttracker.data.repository.LogRepositoryImpl
import com.leeweeder.weighttracker.domain.repository.LogRepository
import com.leeweeder.weighttracker.domain.usecases.LogUseCases
import com.leeweeder.weighttracker.domain.usecases.log.DeleteLogById
import com.leeweeder.weighttracker.domain.usecases.log.GetLogById
import com.leeweeder.weighttracker.domain.usecases.log.GetLogs
import com.leeweeder.weighttracker.domain.usecases.log.InsertLog
import androidx.room.Room
import com.leeweeder.weighttracker.domain.usecases.log.GetFiveMostRecentLogs
import com.leeweeder.weighttracker.domain.usecases.log.UpdateLog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideMyApplicationDatabase(app: Application): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            app,
            AppDatabase::class.java,
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
            deleteLogById = DeleteLogById(repository),
            updateLog = UpdateLog(repository),
            getFiveMostRecentLogs = GetFiveMostRecentLogs(repository)
        )
    }
}