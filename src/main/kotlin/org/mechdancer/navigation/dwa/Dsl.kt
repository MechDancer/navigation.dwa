package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.functions.deflectionTo
import org.mechdancer.navigation.dwa.functions.euclid
import org.mechdancer.navigation.dwa.functions.position
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.log2

data class ConfigurationDsl(
	var period: Double = 0.1,
	var interestAreaRadius: Double = 1.0,
	var speedWindow: ClosedFloatingPointRange<Double> = -0.5..0.5,
	var angularRateWindow: ClosedFloatingPointRange<Double> = -PI / 4..PI / 4,
	var accelerationLimit: Double = 0.1,
	var angularAccelerationLimit: Double = 0.1,
	var speedSampleCount: Int = 3,
	var angularRateSampleCount: Int = 3)

private val default = setOf(
	//终端位置条件
	Condition<Sample>(1.0) { local, trajectory, _ ->
		local.nodes.last() euclid trajectory.nodes.last()
	},
	//终端方向条件
	Condition(1.0) { local, trajectory, _ ->
		(local.nodes.last() deflectionTo trajectory.nodes.last()).absoluteValue
	},
	//全程贴合性条件
	Condition(1.5) { local, trajectory, _ ->
		trajectory
			.nodes
			.map { pathNode -> pathNode.position }
			.sumByDouble { point ->
				local.segments.map { segment -> segment.distanceTo(point) }.min()!!
			}
	},
	//线速度条件
	Condition(1.0) { _, _, speed ->
		log2(speed.first.absoluteValue + speed.second.absoluteValue)
	}
)

fun navigator(conditions: Set<Condition<Sample>> = default,
              block: ConfigurationDsl.() -> Unit) =
	Navigator(
		conditions,
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
