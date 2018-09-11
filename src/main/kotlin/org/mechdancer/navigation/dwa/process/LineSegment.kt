package org.mechdancer.navigation.dwa.process

import org.mechdancer.algebra.vector.impl.Vector2D
import org.mechdancer.navigation.dwa.process.functions.Point
import org.mechdancer.navigation.dwa.process.functions.euclid
import org.mechdancer.navigation.dwa.process.functions.unit

/**
 * 线段
 * 提供与某点距离的计算式
 */
class LineSegment(val source: Point, val target: Point) {
	/** 连线 */
	val connection by lazy { target - source }

	/** 与某点距离 */
	fun distanceTo(point: Point): Double {
		val direction = connection.unit
		val shadow = (point - source) dot direction
		return point euclid when {
			shadow >= connection.norm()
			     -> target
			shadow <= 0
			     -> source
			else -> Vector2D.to2D(source + direction * shadow)
		}
	}
}
