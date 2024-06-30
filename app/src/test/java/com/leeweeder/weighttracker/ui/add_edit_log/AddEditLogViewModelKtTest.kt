package com.leeweeder.weighttracker.ui.add_edit_log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.util.toEpochMilli
import com.leeweeder.weighttracker.util.StartingWeightModel
import com.leeweeder.weighttracker.util.Weight
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AddEditLogViewModelKtTest {

    @Test
    fun `shouldUpdateStartingWeight returns true for first time user`() {
        val startingWeight = StartingWeightModel(0f, 0L, false)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentLog: Log? = null

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight = 150f)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when goal is achieved and most recent log matches goal`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), true)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentLog = Log(weight = Weight(150f), date = currentDate)

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight = 150f)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when current date is before or equal to starting weight date`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), false)
        val currentDate = LocalDate.now().minusDays(10)
        val goalWeight = 150
        val currentWeight = 150f
        val mostRecentLog: Log? = null

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when goal is achieved and most recent log is null but goal weight is achieved`() {
        val startingWeight = StartingWeightModel(0f, 0L, true)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentLog = null

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight = 150f)

        assertTrue(result)
    }
}