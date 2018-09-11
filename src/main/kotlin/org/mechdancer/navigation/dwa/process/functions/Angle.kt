package org.mechdancer.navigation.dwa.process.functions

import org.mechdancer.algebra.vector.impl.Vector2D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** 弧度转方向向量 */
internal val Double.vector get() = Vector2D(cos(this), sin(this))

/** 调整弧度到[-PI, +PI] */
internal tailrec fun Double.adjust(): Double =
	when {
		this > +PI -> (this - 2 * PI).adjust()
		this < -PI -> (this + 2 * PI).adjust()
		else       -> this
	}
