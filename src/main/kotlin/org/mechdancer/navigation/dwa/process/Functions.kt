package org.mechdancer.navigation.dwa.process

import kotlin.math.*

/** 二维向量可以用于表示点 */
internal typealias Point = Vector

/** 速度样点 */
internal typealias Sample = Pair<Double, Double>

/** 条件包括系数和价值函数 */
internal class Condition(val k: Double, val f: (Trajectory, Sample, Trajectory) -> Double)

/** 弧度转方向向量 */
internal val Double.vector get() = Vector(cos(this), sin(this))

//区间运算======================================================================
/** 并集 */
internal operator fun ClosedFloatingPointRange<Double>.plus(other: ClosedFloatingPointRange<Double>) =
	min(start, other.start)..max(endInclusive, other.endInclusive)

/** 交集 */
internal operator fun ClosedFloatingPointRange<Double>.times(other: ClosedFloatingPointRange<Double>) =
	max(start, other.start)..min(endInclusive, other.endInclusive)

/**
 * 等分区间
 * @return 等分点（包括两端点）
 */
internal operator fun ClosedFloatingPointRange<Double>.div(t: Int): List<Double> {
	assert(start < endInclusive)
	assert(t >= 2)
	val step = (endInclusive - start) / (t - 1) //步长
	return List(t) { i -> start + i * step }
}

//构造或变换======================================================================
/** 从谓词构造区域 */
internal fun area(predicate: (Point) -> Boolean) = object : Area {
	override fun contain(point: Point) = predicate(point)
}

/** 从端点构造线段 */
internal val Pair<Point, Point>.segment
	get() = LineSegment(first, second)

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
	source: Node,
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
	val build = { last: Node -> Node(last.position + last.w.vector.rotate(subAngle / 2) * subLength, last.w + subAngle) }
	//初向量
	var variable = source
	var i = 0
	val list = Sequence {
		object : Iterator<Node> {
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

fun main(args: Array<String>) {
	(setOf(1, 2, 3) descartes setOf(4, 5, 6)).forEach(::println)
}
