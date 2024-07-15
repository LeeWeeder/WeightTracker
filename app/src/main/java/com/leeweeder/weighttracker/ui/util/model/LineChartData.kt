package com.leeweeder.weighttracker.ui.util.model

import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer

data class LineChartData(val modelProducer: CartesianChartModelProducer, val dataObserver: () -> Unit)
