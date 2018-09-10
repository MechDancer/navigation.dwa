package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.process.*

/**
 * 导航器
 * @param path 加载路径
 */
internal class Navigator(private val path: Path) {
	/** 加速度界限 */
	private val limit = accelerationLimit to angularAccelerationLimit

	/** 速度窗口界限 */
	private val windows = -speedLimit..+speedLimit to -angularRateLimit..+angularRateLimit

	/** 动态速度窗口，从速度样点扩张得到 */
	private val Sample.dynamic
		get() = (first - limit.first..first + limit.first) to (second - limit.second..second + limit.second)

	/**
	 * 导航
	 * @param current 当前速度
	 * @param node    当前位姿
	 */
	operator fun invoke(
		current: Sample,
		node: Node
	): Sample? {
		//局部规划范围
		val local = path[area { p ->
			val distance = p distanceTo node.position
			distance < interestAreaRadius
		}] ?: return null
		//可能轨迹样点
		val speedList = (current.dynamic.first * windows.first) / speedSampleCount
		val angularRateList = (current.dynamic.second * windows.second) / angularRateSampleCount
		//最优化
		//速率样点集 = {线速度样点集 × 角速度样点集}
		return optimize(local, node, speedList.toSet() descartes angularRateList.toSet())
	}

	private companion object {
		const val givenControlPeriod = 0.1 //单位 秒
		const val interestAreaRadius = 1.0 //一次规划中关心的路径点与当前位置的最大距离
		const val speedLimit = 0.5 //单位 m*s^-1
		const val angularRateLimit = 3.1415927 / 4 //单位 m*s^-1
		const val accelerationLimit = 0.1 //单位 m*s^-2
		const val angularAccelerationLimit = 0.1 //单位 rad*s^-2
		const val speedSampleCount = 3
		const val angularRateSampleCount = 3

		init {
			//最小也得完全包括所有轨迹
			assert(interestAreaRadius > speedLimit * givenControlPeriod)
			//我感觉很合理，好歹也得bangbang控制啊
			assert(speedLimit > accelerationLimit / 2)
			//我感觉很合理，好歹也得bangbang控制啊
			assert(angularRateLimit > angularAccelerationLimit / 2)
			//……
			assert(accelerationLimit > 0)
			//……
			assert(angularAccelerationLimit > 0)
			//至少采到窗口两端
			assert(speedSampleCount >= 2)
			//至少采到窗口两端
			assert(angularRateSampleCount >= 2)
		}
	}
}
