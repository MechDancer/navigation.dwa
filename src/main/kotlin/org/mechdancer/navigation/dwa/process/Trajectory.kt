package org.mechdancer.navigation.dwa.process

import org.mechdancer.navigation.dwa.Sample
import org.mechdancer.navigation.dwa.process.functions.*
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sin

/**
 * 轨迹 := <路点列表, 线段列表>
 * @param nodes 路点列表
 */
class Trajectory(val nodes: List<Pose>) {
	init {
		assert(nodes.size >= 2)
	}

	/**
	 * 规划轨迹
	 * @param source 起始位姿
	 * @param speed  <线速率，角速率>
	 * @param time   规划时间
	 * @param sample 采样点数目
	 * @return 轨迹弧上等间隔的采样点
	 */
	constructor(source: Pose, speed: Sample, time: Double, sample: Int)
		: this(poseSeq(source, speed, time, sample).toList())

	/** 通过差分位置获取线段列表 */
	val segments by lazy {
		(0 until nodes.size - 1)
			.map { LineSegment(nodes[it].position, nodes[it + 1].position) }
	}

	companion object {
		//规划并采样圆弧轨迹
		private fun poseSeq(source: Pose,
		                    speed: Sample,
		                    time: Double,
		                    sample: Int)
			: Sequence<Pose> {
			assert(sample >= 2)
			//弧长
			val length = speed.first * time
			//圆心角
			val angle = speed.second * time
			//相邻线段角度差
			val subAngle = angle / (sample - 1)
			//线段长度
			val subLength =
				if (angle == .0) length / (sample - 1)
				else length.sign * abs(2 * (length / angle) * sin(subAngle / 2))
			//迭代产生下一个位姿
			val next = { last: Pose ->
				val position = last.position + last.direction.rotate(subAngle / 2) * subLength
				Pose(position[0], position[1], last.w + subAngle)
			}
			//初向量
			var variable = source
			var i = 0
			return Sequence {
				object : Iterator<Pose> {
					override fun next() = next(variable)
						.also {
							++i
							variable = it
						}

					override fun hasNext() = i < sample
				}
			}
		}
	}
}
