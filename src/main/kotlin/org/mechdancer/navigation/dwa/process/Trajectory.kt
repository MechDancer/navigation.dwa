package org.mechdancer.navigation.dwa.process

import org.mechdancer.navigation.dwa.process.functions.Pose
import org.mechdancer.navigation.dwa.process.functions.position

/**
 * 轨迹 := <速率样点, 路点列表, 线段列表>
 * @param nodes 路点列表
 */
class Trajectory(
	val nodes: List<Pose>) {
	init {
		assert(nodes.size >= 2)
	}

	val segments by lazy {
		(0 until nodes.size - 1)
			.map { LineSegment(nodes[it].position, nodes[it + 1].position) }
	}
}
