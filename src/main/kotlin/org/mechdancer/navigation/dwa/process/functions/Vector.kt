package org.mechdancer.navigation.dwa.process.functions

import org.mechdancer.algebra.vector.Vector

/** 两点（欧几里得）距离 */
infix fun Vector.euclid(other: Vector) = (this - other).norm()
