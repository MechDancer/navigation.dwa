package org.mechdancer.navigation.dwa.process

import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

/** 二维向量 */
internal data class Vector(val x: Double, val y: Double) {
	operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
	operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
	operator fun times(other: Vector) = x * other.x + y * other.y
	operator fun times(k: Double) = Vector(k * x, k * y)
	operator fun div(k: Double) = this * (1 / k)

	infix fun rotate(angle: Double) = (rad + angle).vector * norm

	val rad by lazy { atan2(this.y, this.x) }
	val norm by lazy { sqrt(x * x + y * y) }

	val unit get() = this / norm

	infix fun distanceTo(other: Point) = (this - other).norm
	infix fun deflectionTo(other: Vector) = acos(unit * other.unit)

	infix fun inside(range: Area) = range.contain(this)
	operator fun unaryMinus() = Vector(-x, -y)
}
