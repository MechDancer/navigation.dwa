package org.mechdancer.navigation.dwa.process

/**
 * 线段
 * 提供与某点距离的计算式
 */
internal class LineSegment(val source: Point, val target: Point) {
	/** 连线 */
	val connection by lazy { target - source }

	/** 与某点距离 */
	fun distanceTo(point: Point) =
		((point - source) * connection.unit)
			.let { shadow ->
				point distanceTo when {
					shadow >= connection.norm
					     -> target
					shadow <= 0
					     -> source
					else -> source + connection.unit * shadow
				}
			}
}
