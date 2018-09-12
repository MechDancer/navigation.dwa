# 速度窗口采样局部路径规划算法
[原理在此](https://blog.csdn.net/heyijia0327/article/details/44983551)，目前本工程尚未实现避障功能。本文主要介绍使用方法和注意事项。

## 前提条件和注意事项

要将 dwa 适配到自己的机器人，请保证机器人具备**全场定位**功能，已为可行驶区域建立笛卡尔坐标系，并能正确获取自身位姿。位姿即机器人坐标系相对场地坐标系的偏移，定义为三维向量[x, y, w]，默认初始化时机器人坐标系与场地坐标系重合，机器人面朝 x 轴方向，符合右手定则。

导航模块假定机器人是**非全向的**，因此无法规划不与机器人坐标系 x 轴相切的路径，但原地转是受支持的。添加对全向的支持、或对个性化的机器人控制方式进行适配并不难，开发者可自行修改。

每次获取控制量需要将机器人当前的控制量和位姿提交到导航器 *navigator* 。导航器将对每个提交的位姿数据计算一个控制量。不会自动插值，也不会进行任务调度，因此请保证外部以合适的频率提交位姿数据，以免发生振荡或延迟。

## 使用

1. 构造导航器

   使用构造器来构造导航器：

   ```kotlin
   val navigator = Navigator(Path(),Configuration( ... ))
   ```

   或使用 DSL 构造导航器（推荐）：

   ```kotlin
   val navigator = navigator { ... }
   ```

   这样构造出来的导航器没有工作路径。

2. 确定全局路径

   本模块的定位是局部路径规划和导航，要实现完整的机器人控制，必须提供合理的全局路径。较为方便的测试方法是“录点”，即通过遥控示教来确定机器人全局路径。本模块内置的路径管理支持录点功能。

   使用语句 `navigator.path += pose`来向工作路径末尾添加一个位姿点。

   调用 `navigator.path.clear()` 将会清空工作路径。

   你也可以访问 `navigator.path` 查看与路径相关的更多功能。

3. 进行控制

   录点完毕后，应首先确定机器人位于工作路径起始点附近。加下来就可以开始向导航器提交控制量和位姿数据，并接收规划控制量。已通过并超出感兴趣区域的位姿点将自动从工作路径中移除。

   提交数据，接收指令：

   ```kotlin
   val cmd = navigator(cmd, pose)
   ```

4. 调整参数

   当前本模块包含两类可调整的参数：

   - 工作参数

     工作参数是路径规划过程中使用的参数，包括感兴趣区域、速度和加速度约束以及控制量采样点数量等，现已收纳到 `Configuration` 数据结构，在导航器构造时通过构造器或 DSL 指定。

     ```kotlin
     val navigator = navigator {
     		period = 1.0
     		interestAreaRadius = 1.0
     		speedWindow = -0.5..+0.5
     		angularRateWindow = -PI / 4..PI / 4
     		accelerationLimit = 0.1
     		angularAccelerationLimit = 0.1
     		speedSampleCount = 3
     		angularRateSampleCount = 3
     	}
     ```

   - 优化参数

     优化参数是选择最优路径的标准，现包含终端位置、终端方向、全程贴合性和速度四项，已收纳到 `Conditions.kt` 中：

     ```kotlin
     private val conditions = setOf(
     	//终端位置条件
     	Condition(1.0) { local, _, trajectory ->
     		local.nodes.last() euclid trajectory.nodes.last()
     	},
     	//终端方向条件
     	Condition(1.0) { local, _, trajectory ->
     		(local.nodes.last() deflectionTo trajectory.nodes.last()).absoluteValue
     	},
     	//全程贴合性条件
     	Condition(1.5) { local, _, trajectory ->
     		trajectory
     			.nodes
     			.map { pathNode -> pathNode.position }
     			.sumByDouble { point ->
     				local.segments.map { 
                         segment -> segment.distanceTo(point) 
                     }.min()!!
     			}
     	},
     	//线速度条件
     	Condition(1.0) { _, speed, _ ->
     		log2(speed.first.absoluteValue + speed.second.absoluteValue)
     	}
     )
     ```

     条件 `Condition` 类定义为：

     ```kotlin
     class Condition(val k: Double, val f: (Trajectory, Sample, Trajectory) -> Double)
     ```

     即优化函数和系数。

     开发者可以修改各项参数，也可以按照这个格式添加自己的优化条件。各优化条件将在最优化函数计算时自动归一化。