package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.process.Trajectory
import org.mechdancer.navigation.dwa.process.TypedTable
import org.mechdancer.navigation.dwa.process.functions.Pose
import org.mechdancer.navigation.dwa.process.functions.deflectionTo
import org.mechdancer.navigation.dwa.process.functions.euclid
import org.mechdancer.navigation.dwa.process.functions.position
import kotlin.math.absoluteValue
import kotlin.math.log2

/** 速度样点 */
typealias Sample = Pair<Double, Double>

/** 条件包括系数和价值函数 */
class Condition(val k: Double, val f: (Trajectory, Sample, Trajectory) -> Double)

private val conditions = setOf(
	//终端位置条件
	Condition(1.0) { local, _, trajectory ->
		local.nodes.last() euclid trajectory.nodes.last()
	},
	//终端方向条件
	Condition(1.0) { local, _, trajectory ->
		(local.nodes.last() deflectionTo trajectory.nodes.last()).absoluteValue
	},
	//全程贴合性条件
	Condition(1.5) { local, _, trajectory ->
		trajectory
			.nodes
			.map { pathNode -> pathNode.position }
			.sumByDouble { point ->
				local.segments.map { segment -> segment.distanceTo(point) }.min()!!
			}
	},
	//线速度条件
	Condition(1.0) { _, speed, _ ->
		log2(speed.first.absoluteValue + speed.second.absoluteValue)
	}
)

/**
 * 最优化函数
 * @param local 局部目标路径
 * @param current 当前位姿
 * @param speeds 速率样点集
 */
internal fun optimize(
	local: Trajectory,
	current: Pose,
	speeds: Set<Pair<Double, Double>>
): Pair<Double, Double> {
	//轨迹集 = { 速率样点 -> 轨迹 }
	//条件-速率样点表 := (条件 × 速率样点) -> 价值
	val table = TypedTable(conditions, speeds)
	{ condition, speed ->
		condition.f(local, speed, Trajectory(current, speed, 1.0, 5))
	}
	//按行计算归一化系数
	val normalizer =
		table.rows.associate { condition ->
			condition to table.row(condition).values.sum()
				.let { if (it > 0) condition.k / it else .0 }
		}
	//最优化
	return table.columns.minBy { speed ->
		table.column(speed)
			.toList()
			.sumByDouble { normalizer[it.first]!! * it.second }
	}!!
}
