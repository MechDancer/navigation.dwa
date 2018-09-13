package org.mechdancer.navigation

import org.mechdancer.navigation.dwa.Path
import org.mechdancer.navigation.dwa.functions.Pose

/**
 * 导航器接口
 * @param T 控制量类型
 */
interface INavigator<T> {
	/**
	 * 管理工作路径
	 */
	val path: Path

	/**
	 * 提交数据，获取控制量
	 * @param current 当前控制量
	 * @param pose    当前位姿
	 * @return 最优控制量
	 */
	operator fun invoke(current: T, pose: Pose): T?
}
