package com.leeweeder.weighttracker.domain.usecases

import com.leeweeder.weighttracker.data.repository.FakeLogRepository
import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.domain.usecases.log.GetLogs
import com.leeweeder.weighttracker.util.Weight
import kotlinx.coroutines.runBlocking
import org.junit.Before
import java.time.LocalDate

class GetLogsTest {

    private lateinit var getLogs: GetLogs
    private lateinit var fakeRepository: FakeLogRepository

    @Before
    fun setUp() {
        fakeRepository = FakeLogRepository()
        getLogs = GetLogs(fakeRepository)

        val logsToInsert = mutableListOf<Log>()
        (1..26).forEachIndexed { _, n ->
            logsToInsert.add(
                Log(
                    date = LocalDate.now(),
                    weight = Weight(0f)
                )
            )
        }
        logsToInsert.shuffle()
        runBlocking {
            logsToInsert.forEach { fakeRepository.insertLog(it) }
        }
    }
}