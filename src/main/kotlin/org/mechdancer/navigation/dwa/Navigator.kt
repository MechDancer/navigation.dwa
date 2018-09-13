package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.INavigator
import org.mechdancer.navigation.dwa.functions.Pose
import org.mechdancer.navigation.dwa.functions.euclid
import org.mechdancer.navigation.dwa.functions.position

/** 速度样点 */
typealias Sample = Pair<Double, Double>

/**
 * 差动导航器
 * 控制量为 <线速度, 角速度>
 * @param conditions 优化条件表
 * @param config     控制参数表
 */
class Navigator(
	private val conditions: Set<Condition<Sample>>,
	private val config: Configuration
) : INavigator<Sample> {
	override val path = Path()
	override operator fun invoke(current: Sample, pose: Pose) =
		path[{ it euclid pose.position < config.interestAreaRadius }]
			?.let { local -> conditions.optimize(local, pose, config.dynamic(current)) }

	/**
	 * 最优化函数
	 * @param local 局部目标路径
	 * @param current 当前位姿
	 * @param speeds 速率样点集
	 */
	private fun Set<Condition<Sample>>.optimize(
		local: Trajectory,
		current: Pose,
		speeds: Set<Pair<Double, Double>>
	): Pair<Double, Double> {
		//轨迹集 = { 速率样点 -> 轨迹 }
		//条件-速率样点表 := (条件 × 速率样点) -> 价值
		val table = TypedTable(this, speeds)
		{ condition, speed ->
			condition.f(local, Trajectory(current, speed, 1.0, 5), speed)
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
}
