package com.leeweeder.weighttracker.ui.util.model

import com.leeweeder.weighttracker.domain.model.Log
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.coroutines.flow.Flow

data class LineChartData(
    val modelProducer: CartesianChartModelProducer,
    val dataObserver: () -> Flow<List<Log>>
)
