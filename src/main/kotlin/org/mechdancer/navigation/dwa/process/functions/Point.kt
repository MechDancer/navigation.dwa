package org.mechdancer.navigation.dwa.process.functions

import org.mechdancer.algebra.vector.impl.Vector2D
import org.mechdancer.navigation.dwa.process.Area

/** 用二维向量表示位置 */
typealias Point = Vector2D

/** 判断点是否在区域内 */
infix fun Point.inside(range: Area) = range.contain(this)
