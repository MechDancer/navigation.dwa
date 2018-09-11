package org.mechdancer.navigation.dwa.process

import org.mechdancer.navigation.dwa.process.functions.Pose
import org.mechdancer.navigation.dwa.process.functions.inside
import org.mechdancer.navigation.dwa.process.functions.position
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 路径
 * 来自全局路径规划或录点法
 * 可以向尾部再添加路点
 * 提供方法获取工作路径
 */
class Path(list: List<Pose> = listOf()) {
	private val _list = ConcurrentLinkedQueue(list)

	/** 获取剩余路径 */
	val list get() = _list.toList()

	/** 在尾部添加新位姿 */
	operator fun plusAssign(pose: Pose) {
		_list.add(pose)
	}

	/** 获取工作区路径，并丢弃已超出工作区的位姿 */
	operator fun get(area: Area): Trajectory? {
		//出区舍尾
		while (_list.firstOrNull()?.takeIf { it.position inside area } != null)
			_list.remove()
		val local = _list.takeWhile { it.position inside area }
		return if (local.size < 2) null else Trajectory(local)
	}
}
