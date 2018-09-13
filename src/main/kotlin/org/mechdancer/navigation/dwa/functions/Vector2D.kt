package org.mechdancer.navigation.dwa.functions

import org.mechdancer.algebra.vector.Axis3D.X
import org.mechdancer.algebra.vector.Axis3D.Y
import org.mechdancer.algebra.vector.impl.Vector2D
import kotlin.math.acos
import kotlin.math.atan2

/** 方向向量转弧度 */
val Vector2D.rad get() = atan2(this[Y], this[X])

/** 旋转向量方向 */
infix fun Vector2D.rotate(angle: Double) = (rad + angle).vector * norm()

/** 两方向向量夹角 */
infix fun Vector2D.deflectionTo(other: Vector2D) = acos(normalize() dot other.normalize())
