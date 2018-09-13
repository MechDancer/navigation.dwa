package org.mechdancer.navigation.dwa

import org.mechdancer.navigation.dwa.functions.descartes
import java.util.*

/**
 * 类型化二维表（二维场）
 * @param TR      行类型
 * @param TC      列类型
 * @param TV      值类型
 * @param rows    行元素集
 * @param columns 列元素集
 * @param block   转换函数
 */
class TypedTable<TR, TC, TV>(val rows: Set<TR>,
                             val columns: Set<TC>,
                             block: (TR, TC) -> TV
) {
	//位置 -> 值映射表
	private val field =
		(rows descartes columns)
			.associate { it to block(it.first, it.second) }

	/** @return 查找表项 */
	operator fun get(pair: Pair<TR, TC>) =
		if (pair in field) Optional.of(field[pair]!!)
		else Optional.empty()

	/** @return 查找某行上所有项 */
	fun row(r: TR) =
		field.filter { it.key.first == r }
			.toList()
			.associate { it.first.second to it.second }

	/** @return 查找某列上所有项 */
	fun column(c: TC) =
		field.filter { it.key.second == c }
			.toList()
			.associate { it.first.first to it.second }
}
