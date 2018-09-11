package org.mechdancer.navigation.dwa.process

import org.mechdancer.algebra.vector.Axis3D.X
import org.mechdancer.algebra.vector.Axis3D.Y
import org.mechdancer.algebra.vector.Vector
import org.mechdancer.algebra.vector.impl.Vector2D.Companion.to2D
import org.mechdancer.navigation.dwa.process.functions.*
import kotlin.math.*

/** 速度样点 */
internal typealias Sample = Pair<Double, Double>

/** 求方向向量 */
val Vector.unit get() = this / norm()

/** 两点（欧几里得）距离 */
infix fun Vector.distanceTo(other: Vector) = (this - other).norm()

/** 弧度转方向向量 */
internal val Double.vector get() = Point(cos(this), sin(this))

//构造或变换======================================================================
/** 从谓词构造区域 */
internal fun area(predicate: (Point) -> Boolean) = object : Area {
	override fun contain(point: Point) = predicate(point)
}

/**
 * 距离计算
 * 1. 欧式距离
 * 2. 曼哈顿距离
 */
enum class DistanceType(val distance: (Pair<List<Double>, List<Double>>) -> Double) {
	Euclid({ pair ->
		pair.check()
		pair.first.indices.sumByDouble { i -> (pair.first[i] - pair.second[i]).let { it * it } }
	}),
	Manhattan({ pair ->
		pair.check()
		pair.first.indices.sumByDouble { i -> (pair.first[i] - pair.second[i]).absoluteValue }
	});

	private companion object {
		private fun Pair<List<Double>, List<Double>>.check() {
			assert(first.size == second.size)
		}
	}
}

/** 求两集合的笛卡尔积 */
internal infix fun <A, B> Set<A>.descartes(other: Set<B>) =
	flatMap { a -> other.map { b -> a to b } }.toSet()

/** 调整角度到[-PI, +PI] */
internal tailrec fun Double.adjust(): Double =
	when {
		this > +PI -> (this - 2 * PI).adjust()
		this < -PI -> (this + 2 * PI).adjust()
		else       -> this
	}

/**
 * 规划轨迹
 * @param source 起始位姿
 * @param speed  <线速率，角速率>
 * @param time   规划时间
 * @param sample 采样点数目
 * @return 轨迹弧上等间隔的采样点
 */
internal fun trajectory(
	source: Pose,
	speed: Sample,
	time: Double,
	sample: Int
): Trajectory {
	assert(sample >= 2)
	val length = speed.first * time //弧长
	val angle = speed.second * time //圆心角
	//相邻线段角度差
	val subAngle = angle / (sample - 1)
	val subLength =
		if (angle == .0)
			length / (sample - 1)
		else
			length.sign * abs(2 * (length / angle) * sin(subAngle / 2))
	val build = { last: Pose ->
		val position = to2D(last.position + last.direction.rotate(subAngle / 2) * subLength)
		Pose(position[X], position[Y], last.w + subAngle)
	}
	//初向量
	var variable = source
	var i = 0
	val list = Sequence {
		object : Iterator<Pose> {
			override fun next() = build(variable)
				.also {
					++i
					variable = it
				}

			override fun hasNext() = i < sample
		}
	}.toList()
	return Trajectory(list)
}
