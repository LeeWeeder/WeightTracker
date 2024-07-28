package com.leeweeder.weighttracker.ui.add_edit_log

import com.leeweeder.weighttracker.util.Weight

data class WeightState(private val weight: Weight) {
    var logWeight = weight
        private set(value) {
            field = value
            potentialWeight = value
        }

    var potentialWeight = weight
        private set

    fun setPotentialWeight(value: Weight): WeightState {
        return this.copy().apply {
            potentialWeight = value
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is WeightState && this.potentialWeight == other.potentialWeight
    }

    override fun hashCode(): Int {
        return potentialWeight.hashCode()
    }
}

fun Weight.toWeightState() = WeightState(this)
