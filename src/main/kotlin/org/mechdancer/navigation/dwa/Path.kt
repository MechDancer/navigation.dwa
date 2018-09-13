package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.functions.Point
import org.mechdancer.navigation.dwa.functions.Pose
import org.mechdancer.navigation.dwa.functions.position
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 路径
 * 来自全局路径规划或录点法
 * 可以向尾部再添加路点
 * 提供方法获取工作路径
 */
class Path {
	//全局路径存储
	private val _list = ConcurrentLinkedQueue<Pose>()

	/** 获取剩余路径 */
	val list get() = _list.toList()

	/** 在尾部添加新位姿 */
	operator fun plusAssign(pose: Pose) {
		_list.add(pose)
	}

	/** 获取工作区路径，并丢弃已超出工作区的位姿 */
	operator fun get(area: (Point) -> Boolean): Trajectory? {
		//出区舍尾
		while (_list.firstOrNull()?.position?.takeUnless(area) != null)
			_list.remove()
		val local = _list.takeWhile { area(it.position) }
		return if (local.size < 2) null else Trajectory(local)
	}

	/** 清空路径 */
	fun clear() {
		_list.clear()
	}

	/** 加载新路径 */
	fun load(list: List<Pose>) {
		_list.clear()
		_list.addAll(list)
	}
}
