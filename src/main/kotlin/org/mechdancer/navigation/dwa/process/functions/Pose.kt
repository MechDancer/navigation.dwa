package org.mechdancer.navigation.dwa.process.functions

import org.mechdancer.algebra.vector.Axis3D.*
import org.mechdancer.algebra.vector.impl.Vector3D
import org.mechdancer.navigation.dwa.process.adjust
import org.mechdancer.navigation.dwa.process.distanceTo
import org.mechdancer.navigation.dwa.process.vector

/** 用三维向量表示位姿 */
typealias Pose = Vector3D

/** 转角 */
val Vector3D.w get() = this[Z].adjust()

/** 位置 */
val Vector3D.position get() = Point(this[X], this[Y])

/** 方向 */
val Vector3D.direction get() = this[Z].vector

/** 欧几里得距离 */
infix fun Vector3D.distanceTo(other: Pose) = position distanceTo other.position

/** 方向夹角 */
infix fun Vector3D.deflectionTo(other: Pose) = direction deflectionTo other.direction
