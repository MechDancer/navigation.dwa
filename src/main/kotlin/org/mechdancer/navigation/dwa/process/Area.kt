package org.mechdancer.navigation.dwa.process

/**
 * 二维区域
 * 提供谓词判断点是否在区域内
 */
internal interface Area {
	/** 判断区域内是否有某点 */
	fun contain(point: Point): Boolean
}
