package org.mechdancer.navigation.dwa.process

/**
 * 点 := <位置, 方向>
 * @param x  位置横坐标
 * @param y  位置纵坐标
 * @param w0 方向与极轴夹角
 */
internal class Node(
	val x: Double,
	val y: Double,
	w0: Double
) {
	constructor(point: Point, w0: Double) : this(point.x, point.y, w0)

	val w = w0.adjust()

	val list by lazy { listOf(x, y, w) }
	val position by lazy { Vector(x, y) }

	infix fun distanceTo(other: Node) = position distanceTo other.position
	infix fun deflectionTo(other: Node) = w.vector deflectionTo other.w.vector

	override fun toString() =
		{ x: Double -> x.toString().take(5) }
			.let { "x: ${it(x)}m, y: ${it(y)}m, w: ${it(w)}rad" }
}
