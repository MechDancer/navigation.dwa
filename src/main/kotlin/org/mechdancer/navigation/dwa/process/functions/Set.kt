package org.mechdancer.navigation.dwa.process.functions

import kotlin.math.max
import kotlin.math.min

/** 并集 */
operator fun ClosedFloatingPointRange<Double>.plus(other: ClosedFloatingPointRange<Double>) =
	min(start, other.start)..max(endInclusive, other.endInclusive)

/** 交集 */
operator fun ClosedFloatingPointRange<Double>.times(other: ClosedFloatingPointRange<Double>) =
	max(start, other.start)..min(endInclusive, other.endInclusive)

/**
 * 等分区间
 * @return 等分点（包括两端点）
 */
operator fun ClosedFloatingPointRange<Double>.div(t: Int): List<Double> {
	assert(start < endInclusive)
	assert(t >= 2)
	val step = (endInclusive - start) / (t - 1) //步长
	return List(t) { i -> start + i * step }
}

/** 求两离散集合的笛卡尔积 */
internal infix fun <A, B> Set<A>.descartes(other: Set<B>) =
	flatMap { a -> other.map { b -> a to b } }.toSet()
