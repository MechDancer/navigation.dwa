package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.process.Area.Companion.area
import org.mechdancer.navigation.dwa.process.Path
import org.mechdancer.navigation.dwa.process.functions.Pose
import org.mechdancer.navigation.dwa.process.functions.euclid
import org.mechdancer.navigation.dwa.process.functions.position

/**
 * 导航器
 * @param path 加载路径
 */
class Navigator(val path: Path, val config: Configuration) {
	/**
	 * 导航
	 * @param speed 当前速度
	 * @param pose  当前位姿
	 * @return 最优速度组合
	 */
	operator fun invoke(
		speed: Sample,
		pose: Pose
	) = path[area { it euclid pose.position < config.interestAreaRadius }]
		?.let { local -> optimize(local, pose, config.dynamic(speed)) }
}
