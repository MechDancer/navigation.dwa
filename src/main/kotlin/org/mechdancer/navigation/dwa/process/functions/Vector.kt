package org.mechdancer.navigation.dwa.process.functions

import org.mechdancer.algebra.vector.Vector

/** 求方向向量 */
val Vector.unit get() = this / norm()

/** 两点（欧几里得）距离 */
infix fun Vector.euclid(other: Vector) = (this - other).norm()
