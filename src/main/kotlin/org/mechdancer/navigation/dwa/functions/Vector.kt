package org.mechdancer.navigation.dwa.functions

import org.mechdancer.algebra.vector.Vector
import org.mechdancer.algebra.vector.impl.Vector2D

/** 用二维向量表示位置 */
typealias Point = Vector2D

/** 两点（欧几里得）距离 */
infix fun Vector.euclid(other: Vector) = (this - other).norm()
