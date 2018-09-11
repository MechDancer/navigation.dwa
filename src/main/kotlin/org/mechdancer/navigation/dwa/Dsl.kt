package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.process.Path
import kotlin.math.PI

data class ConfigurationDsl(
	var period: Double = 0.1,
	var interestAreaRadius: Double = 1.0,
	var speedWindow: ClosedFloatingPointRange<Double> = -0.5..0.5,
	var angularRateWindow: ClosedFloatingPointRange<Double> = -PI / 4..PI / 4,
	var accelerationLimit: Double = 0.1,
	var angularAccelerationLimit: Double = 0.1,
	var speedSampleCount: Int = 3,
	var angularRateSampleCount: Int = 3)

fun navigator(block: ConfigurationDsl.() -> Unit) =
	Navigator(
		Path(),
		ConfigurationDsl().apply(block).let {
			Configuration(
				it.period,
				it.interestAreaRadius,
				it.speedWindow,
				it.angularRateWindow,
				it.accelerationLimit,
				it.angularAccelerationLimit,
				it.speedSampleCount,
				it.angularRateSampleCount)
		})
