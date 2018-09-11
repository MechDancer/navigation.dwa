package org.mechdancer.navigation.dwa.process

import java.util.*

/**
 * 类型化二维表（二维场）
 * @param TR 行类型
 * @param TC 列类型
 * @param TV 值类型
 */
class TypedTable<TR, TC, TV>
(val rows: Set<TR>, val columns: Set<TC>, block: (TR, TC) -> TV) {
	private val set by lazy { rows descartes columns }
	private val value by lazy { set.associate { it to block(it.first, it.second) } }

	/** @return 表项 */
	operator fun get(pair: Pair<TR, TC>) =
		if (pair in value) Optional.of(value[pair]!!)
		else Optional.empty()

	/** @return 某行上所有项 */
	fun row(r: TR) =
		value.filter { it.key.first == r }
			.toList()
			.associate { it.first.second to it.second }

	/** @return 某列上所有项 */
	fun column(c: TC) =
		value.filter { it.key.second == c }
			.toList()
			.associate { it.first.first to it.second }
}
