//package org.mechdancer.navigation.dwa
//
//import cn.autolabor.core.annotation.InjectMessage
//import cn.autolabor.core.annotation.SubscribeMessage
//import cn.autolabor.core.annotation.TaskFunction
//import cn.autolabor.core.annotation.TaskProperties
//import cn.autolabor.core.server.ServerManager
//import cn.autolabor.core.server.executor.AbstractTask
//import cn.autolabor.core.server.message.MessageHandle
//import cn.autolabor.driver.chassis.AutolaborProIDriver
//import cn.autolabor.message.navigation.Msg2DOdometry
//import cn.autolabor.message.navigation.Msg2DTwist
//import cn.autolabor.message.sensor.MsgJoystick
//import cn.autolabor.module.driver.joystick.LogietchF710X
//import cn.autolabor.module.tool.Joy2Twist
//import org.mechdancer.navigation.dwa.process.*
//import org.mechdancer.navigation.dwa.process.DistanceType.Manhattan
//import java.util.concurrent.atomic.AtomicBoolean
//
//@TaskProperties
//class Task : AbstractTask("JoyListener") {
//	//模式选择
//	private val recordMode = AtomicBoolean(false)
//	private val _autoMode = AtomicBoolean(false)
//
//	/** 自动模式状态转移 */
//	private var autoMode
//		get() = _autoMode.get()
//		set(value) {
//			if (_autoMode.getAndSet(value) == value) return
//			Joy2Twist.setSendFlag(!value)
//			if (value) {
//				info = Triple(System.nanoTime(), .0, .0)
//				control()
//			} else {
//				info = Triple(-1, .0, .0)
//			}
//		}
//
//	//路点
//	private val list = mutableListOf(Node(.0, .0, .0))
//	private var path = Path(list)
//
//	//控制
//	@InjectMessage(topic = "cmd_vel")
//	private var twistMessageHandle: MessageHandle<Msg2DTwist>? = null
//
//	private var info = Triple(-1L, .0, .0)
//		get() {
//			synchronized(this) {
//				return field
//			}
//		}
//		set(value) {
//			synchronized(this) {
//				field = value
//			}
//		}
//
//	class Lowpass(private val k: Double) {
//		private var memory = .0
//		operator fun invoke(value: Double): Double {
//			memory = memory * k + value * (1 - k)
//			return memory
//		}
//	}
//
//	private val lowpass = Lowpass(.0)
//
//	@TaskFunction(name = "control")
//	fun control() {
//		if (info.first > 0) {
//			twistMessageHandle?.pushSubData(
//				Msg2DTwist(info.second, .0, lowpass(info.third), "base_link"))
//			ServerManager.me().delayRun(this, 100, "control")
//		}
//	}
//
//	private companion object {
//		const val givenControlPeriod = 0.1 //单位 秒
//		const val interestAreaRadius = 1.0 //一次规划中关心的路径点与当前位置的最大距离
//		const val speedLimit = 0.5 //单位 m*s^-1
//		const val angularRateLimit = 3.1415927 / 4 //单位 m*s^-1
//		const val accelerationLimit = 0.1 //单位 m*s^-2
//		const val angularAccelerationLimit = 0.1 //单位 rad*s^-2
//		const val speedSampleCount = 3
//		const val angularRateSampleCount = 3
//
//		init {
//			//最小也得完全包括所有轨迹
//			assert(interestAreaRadius > speedLimit * givenControlPeriod)
//			//我感觉很合理，好歹也得bangbang控制啊
//			assert(speedLimit > accelerationLimit / 2)
//			//我感觉很合理，好歹也得bangbang控制啊
//			assert(angularRateLimit > angularAccelerationLimit / 2)
//			//……
//			assert(accelerationLimit > 0)
//			//……
//			assert(angularAccelerationLimit > 0)
//			//至少采到窗口两端
//			assert(speedSampleCount >= 2)
//			//至少采到窗口两端
//			assert(angularRateSampleCount >= 2)
//		}
//	}
//
//	@SubscribeMessage(topic = "odom")
//	@TaskFunction(name = "record")
//	@kotlin.jvm.Synchronized
//	fun record(odom: Msg2DOdometry) {
//		val node = Node(odom.pose.x, odom.pose.y, odom.pose.yaw)
//		if (recordMode.get()) {
//			if (list.isEmpty())
//				list += node
//			else
//				Manhattan.distance(node.list to list.last().list)
//					.let { if (it in 0.05..0.5) list += node }
//		}
//		if (!autoMode) return
//
//		//局部规划范围
//		val local = path[area { p ->
//			val distance = p distanceTo node.position
//			distance < interestAreaRadius
//		}]
//		if (local == null) {
//			autoMode = false
//			return
//		}
//		//可能轨迹采样窗口
//		val speedWindow = (info.second - accelerationLimit..info.second + accelerationLimit) * (-speedLimit..+speedLimit)
//		val angularRateWindow = (info.third - angularAccelerationLimit..info.third + angularAccelerationLimit) * (-angularRateLimit..+angularRateLimit)
//		//可能轨迹样点
//		val speedList = speedWindow / speedSampleCount
//		val angularRateList = angularRateWindow / angularRateSampleCount
//		//速率样点集 = {线速度样点集 × 角速度样点集}
//		//最优化
//		val optimization = optimize(local, node, speedList.toSet() descartes angularRateList.toSet())
//		println("speed = ${optimization.first}, angular rate = ${optimization.second}")
//		info = Triple(System.nanoTime(), optimization.first, optimization.second)
//	}
//
//	/**
//	 * 状态转移
//	 * button “3” pressed            -> auto
//	 * button “4” pressed when !auto -> record
//	 */
//	@SubscribeMessage(topic = "joy")
//	@TaskFunction(name = "trans")
//	fun trans(msg: MsgJoystick) {
//		//自动状态转移
//		autoMode = msg.buttons[4].toInt() == 1
//		//记录状态转移
//		(msg.buttons[5].toInt() == 1 && !autoMode).let { enable ->
//			if (recordMode.getAndSet(enable) != enable)
//				if (enable)
//					list.clear()
//				else
//					path = Path(list).apply { list.forEachIndexed { i, it -> println("$i: $it") } }
//		}
//	}
//}
//
//fun main(args: Array<String>) {
//	LogietchF710X()       //监听
//	Joy2Twist.setSendFlag(true)
//	AutolaborProIDriver() //控制
//	Task()                //响应
//}
