package org.mechdancer.navigation.dwa.process

/**
 * 轨迹 := <速率样点, 路点列表, 线段列表>
 * @param nodes 路点列表
 */
internal class Trajectory(
	val nodes: List<Node>) {
	init {
		assert(nodes.size >= 2)
	}

	val segments by lazy {
		(0 until nodes.size - 1)
			.map { (nodes[it].position to nodes[it + 1].position).segment }
	}
}
