package org.mechdancer.navigation.dwa.process

import java.util.*

/**
 * 路径
 * 来自全局路径规划或录点法
 * 提供获取感兴趣区方法
 * ****************
 * > 线程不安全！
 * > 非不可变类！
 * > 不可多线程使用！
 * ****************
 */
internal class Path(list: List<Node>) {
	private val _list = LinkedList(list)
	val list get() = _list.toList()
	val size = list.size

	operator fun get(area: Area): Trajectory? {
		//出区舍尾
		while (_list.isNotEmpty() && !(_list.first().position inside area))
			_list.remove()
		val local = mutableListOf<Node>()
		for (i in _list.indices) {
			if (!(_list[i].position inside area)) break
			local += _list[i]
		}
		return if (local.size < 2) null else Trajectory(local)
	}
}