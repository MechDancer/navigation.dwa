package org.mechdancer.navigation.dwa.functions

import org.mechdancer.algebra.vector.Axis3D.*
import org.mechdancer.algebra.vector.impl.Vector3D

/** 用三维向量表示位姿 */
typealias Pose = Vector3D

/** 转角 */
val Pose.w get() = this[Z].adjust()

/** 位置 */
val Pose.position get() = Point(this[X], this[Y])

/** 方向 */
val Pose.direction get() = this[Z].vector

/** 欧几里得距离 */
infix fun Pose.euclid(other: Pose) = position euclid other.position

/** 方向夹角 */
infix fun Pose.deflectionTo(other: Pose) = direction deflectionTo other.direction
