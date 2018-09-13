package org.mechdancer.navigation.dwa

/** 条件包括系数和价值函数 */
class Condition<T>(val k: Double, val f: (Trajectory, Trajectory, T) -> Double)
