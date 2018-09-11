package org.mechdancer.navigation.dwa.process

import org.mechdancer.navigation.dwa.process.functions.Point

/**
 * 二维区域
 * 提供谓词判断点是否在区域内
 */
interface Area {
	/** 判断区域内是否有某点 */
	fun contain(point: Point): Boolean

	companion object {
		/** 从谓词构造区域 */
		fun area(predicate: (Point) -> Boolean) = object : Area {
			override fun contain(point: Point) = predicate(point)
		}
	}
}
