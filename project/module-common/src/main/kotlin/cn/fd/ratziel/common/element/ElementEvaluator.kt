package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.element.ElementEvaluator.cycledEvaluations
import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.ElementRegistry
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementIdentifier
import kotlinx.coroutines.*
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Supplier
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * ElementEvaluator - 元素评估器
 *
 * @author TheFloodDragon
 * @since 2025/4/25 21:32
 */
object ElementEvaluator {

    /**
     * 协程作用域
     */
    val scope = CoroutineScope(CoroutineName("ElementEvaluator") + Dispatchers.Default)

    /**
     * 周期性评估任务组表
     */
    val cycledEvaluations: MutableMap<LifeCycle, EvaluationGroup> = HashMap<LifeCycle, EvaluationGroup>()
        .apply { for (cycle in LifeCycle.entries) put(cycle, EvaluationGroup()) } // 初始化任务组表

    /**
     * 加载过的元素表
     */
    val evaluatedElements: MutableMap<ElementIdentifier, Element> = ConcurrentHashMap()

    /**
     * 处理任务
     */
    fun handleTask(task: EvaluationTask): Result<Element> {
        return try {
            val element = task.element
            // 处理元素
            task.handler.handle(element)
            // 缓存加载过的元素
            evaluatedElements[element.identifier] = element
            // 完成任务
            Result.success(element)
        } catch (ex: Throwable) {
            severe("Couldn't handle element by ${task.handler}!")
            ex.printStackTrace()
            Result.failure(ex)
        }
    }

    /**
     * 评估 [cycledEvaluations] 内的所有任务
     *
     * @return 返回 [java.util.concurrent.CompletableFuture], 包含处理总共耗时
     */
    fun evaluateCycled(): CompletableFuture<Duration> {
        val cycleTasks = ArrayList<CompletableFuture<Duration>>()
        // 遍历生命周期创建周期任务
        for ((lifeCycle, group) in cycledEvaluations) {
            // 没任务直接下一个
            if (group.evaluations.isEmpty()) continue
            // 创建周期任务回调
            val future = CompletableFuture<Duration>().also { cycleTasks.add(it) }
            // 注册周期任务
            TabooLib.registerLifeCycleTask(lifeCycle, 10) {
                // 开启阻塞协程
                runBlocking {
                    // 开始记录时间
                    val timeMark = TimeSource.Monotonic.markNow()
                    // 评估组内所有任务
                    group.evaluate()
                    // 完成并回传处理时间
                    future.complete(timeMark.elapsedNow())
                }
            }
        }
        return CompletableFuture.allOf(*cycleTasks.toTypedArray()).thenApply {
            // 合并时间
            var duration = Duration.Companion.ZERO
            for (t in cycleTasks) {
                duration = t.join() // 此时已经是所有任务结束后了
            }
            duration
        }
    }

    /**
     * 提交周期评估任务
     */
    fun submitCycledTask(element: Element) {
        val task = createTask(element)
        // 注册到任务组里
        val group = cycledEvaluations[task.config.lifeCycle]!!
        group.evaluations.add(task)
    }

    /**
     * 创建任务
     */
    fun createTask(element: Element): EvaluationTask {
        val handler = ElementRegistry[element.type]
        return EvaluationTask(element, handler, findConfig(handler))
    }

    /**
     * 清空
     */
    fun clear() {
        cycledEvaluations.forEach { it.value.evaluations.clear() }
        evaluatedElements.clear()
    }

    /**
     * 评估任务组
     */
    class EvaluationGroup(val evaluations: MutableCollection<EvaluationTask> = ConcurrentLinkedQueue()) {

        suspend fun evaluate(): List<Result<Element>> {
            // 异步任务列表
            val asyncTasks = ArrayList<Deferred<Result<Element>>>()
            // 同步任务列表
            val syncTasks = ArrayList<Supplier<Result<Element>>>()
            // 提交任务
            for (task in evaluations) {
                // 异步 & 同步 任务提交
                if (task.config.async) {
                    asyncTasks.add(scope.async { handleTask(task) })
                } else {
                    syncTasks.add(Supplier { handleTask(task) })
                }
            }
            // 执行同步任务, 完成后取得结果
            val syncResults = syncTasks.map { it.get() }
            // 等待所有异步任务完成 (必须在同步之后再等待), 并返回结果
            val asyncResults = awaitAll(*asyncTasks.toTypedArray())
            // 返回最终结果
            return syncResults + asyncResults
        }

    }

    /**
     * 评估任务
     */
    class EvaluationTask(
        /** 要评估的元素 **/
        val element: Element,
        /** 元素处理器 **/
        val handler: ElementHandler,
        /** 元素配置 **/
        val config: ElementConfig
    )

    /**
     * 获取 [ElementHandler] 的 [ElementConfig]
     */
    private fun findConfig(handler: ElementHandler): ElementConfig {
        // 分析注解
        val handlerClass = handler::class.java
        val annoClass = ElementConfig::class.java
        val method = handlerClass.getMethod("handle", Element::class.java)
        val config = when {
            // 处理函数注解
            method.isAnnotationPresent(annoClass) -> method.getAnnotation(annoClass)
            // 类注解
            handlerClass.isAnnotationPresent(annoClass) -> handlerClass.getAnnotation(annoClass)
            // 默认配置
            else -> ElementConfig()
        }
        return config
    }

}