package org.mechdancer.navigation.dwa.process

import org.mechdancer.navigation.dwa.process.functions.Point

/**
 * 二维区域
 * 提供谓词判断点是否在区域内
 */
interface Area {
	/** 判断区域内是否有某点 */
	fun contain(point: Point): Boolean
}
