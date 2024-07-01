package com.leeweeder.weighttracker.ui.add_edit_log

import com.leeweeder.weighttracker.domain.model.Log
import com.leeweeder.weighttracker.ui.util.toEpochMilli
import com.leeweeder.weighttracker.util.StartingWeightModel
import com.leeweeder.weighttracker.util.Weight
import org.junit.Assert.assertFalse
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

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight = 160f)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when user not achieved a goal and current record is older than starting weight`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), false)
        val currentDate = LocalDate.now().minusDays(10)
        val goalWeight = 150
        val mostRecentWeight = 160.5f
        val mostRecentLog = Log(weight = Weight(mostRecentWeight), date = LocalDate.now().minusDays(5))

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, 160f)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when user not achieved a goal and current record's date is equal to starting weight`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().toEpochMilli(), false)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentWeight = 160.5f
        val mostRecentLog = Log(weight = Weight(mostRecentWeight), date = LocalDate.now().minusDays(5))

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, 160f)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns false when user not achieved a goal and current record is earlier than starting weight`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), false)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentWeight = 160.5f
        val mostRecentLog = Log(weight = Weight(mostRecentWeight), date = LocalDate.now().minusDays(5))

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, 160f)

        assertFalse(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when user not achieved a goal but the current record surpass the goal`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), false)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentWeight = 160f
        val mostRecentLog = Log(weight = Weight(mostRecentWeight), date = LocalDate.now().minusDays(5))

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, 140f)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when current date is before starting weight date and goal was not yet achieved`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), false)
        val currentDate = LocalDate.now().minusDays(10)
        val goalWeight = 150
        val currentWeight = 160.5f
        val mostRecentLog: Log? = null

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight)

        assertTrue(result)
    }

    @Test
    fun `shouldUpdateStartingWeight returns true when goal is achieved but it is the first record that achieved the goal`() {
        val startingWeight = StartingWeightModel(0f, 0L, true)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentLog = null

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
    fun `shouldUpdateStartingWeight returns false when goal is achieved and most recent log does not achieved the goal`() {
        val startingWeight = StartingWeightModel(160f, LocalDate.now().minusDays(5).toEpochMilli(), true)
        val currentDate = LocalDate.now()
        val goalWeight = 150
        val mostRecentLog = Log(weight = Weight(151f), date = currentDate.minusDays(4))

        val result = shouldUpdateStartingWeight(startingWeight, currentDate, goalWeight, mostRecentLog, currentWeight = 150f)

        assertFalse(result)
    }
}