package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.WorkspaceLoader
import cn.fd.ratziel.common.element.ElementEvaluator.evaluations
import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.ElementRegistry
import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.element.ElementIdentifier
import cn.fd.ratziel.core.element.ElementType
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.platform.function.debug
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
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
     * 线程池
     */
    val executor: ExecutorService get() = WorkspaceLoader.executor

    /**
     * 协程作用域
     */
    val scope = CoroutineScope(CoroutineName("ElementEvaluator") + executor.asCoroutineDispatcher())

    /**
     * 评估任务表
     */
    val evaluations: MutableMap<ElementType, EvaluationGroup> = ConcurrentHashMap()

    /**
     * 评估过的元素表 (不包含评估失败的)
     */
    val evaluatedElements: MutableMap<ElementIdentifier, Element> = ConcurrentHashMap()

    /**
     * 直接处理元素
     */
    fun handleElement(element: Element): Throwable? = runBlocking {
        this@ElementEvaluator.handleElement(ElementRegistry[element.type], element)
    }

    /**
     * 使用 [handler] 处理元素 [element]
     */
    suspend fun handleElement(handler: ElementHandler, element: Element): Throwable? {
        // 触发 ElementEvaluateEvent.Process
        ElementEvaluateEvent.Process(handler, element).call()
        try {
            // 处理元素
            handler.update(element)
            // 缓存加载过的元素
            evaluatedElements[element.identifier] = element
            return null
        } catch (ex: Throwable) {
            severe("Couldn't handle element '${element.name}' by $handler!", ex.stackTraceToString())
            return ex
        }
    }

    /**
     * 评估 [evaluations] 内的所有任务
     *
     * @return 返回 [CompletableFuture], 包含处理总共耗时
     */
    fun evaluateCycled(): CompletableFuture<Duration> {
        val cycleTasks = ArrayList<CompletableFuture<Duration>>()
        // 遍历生命周期创建周期任务
        for (lifeCycle in LifeCycle.entries.filter { it.ordinal >= LifeCycle.LOAD.ordinal }) {
            // 寻找当前生命周期执行的所有任务
            val groups = ArrayList<EvaluationGroup>()
            for (group in evaluations.values) {
                if (group.lifeCycle == lifeCycle) {
                    groups.add(group)
                }
            }
            // 没有对应周期的元素组就跳过
            if (groups.isEmpty()) continue
            // 创建周期任务
            val future = CompletableFuture<Duration>().also { cycleTasks.add(it) }
            // 注册周期任务
            TabooLib.registerLifeCycleTask(lifeCycle, 10) {
                // 开始记录时间
                val timeMark = TimeSource.Monotonic.markNow()
                // 评估所有组内的所有任务
                val tasks = groups.map { it.evaluate().asCompletableFuture() }
                // 合并任务
                CompletableFuture.allOf(*tasks.toTypedArray()).thenRun {
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
     * 提交任务
     */
    fun submit(element: Element) {
        // 获取任务组
        val group = evaluations.computeIfAbsent(element.type) {
            val handler = ElementRegistry[element.type]
            EvaluationGroup(
                handler,
                findConfig(handler),
            )
        }
        // 提交元素
        group.elements.add(element)
    }

    /**
     * 清空
     */
    fun clear() {
        evaluations.clear()
        evaluatedElements.clear()
    }

    /**
     * 评估任务组
     */
    class EvaluationGroup(
        /** 元素处理器 **/
        val handler: ElementHandler,
        /** 元素配置 **/
        val config: ElementConfig,
    ) {

        /**
         * 要评估的元素列表
         */
        val elements: MutableCollection<Element> = ConcurrentLinkedQueue()

        /**
         * 生命周期
         */
        val lifeCycle: LifeCycle get() = config.lifeCycle

        /**
         * 所有任务是否完成
         */
        private var isDone: Boolean = false

        /**
         * 评估所有任务 (只执行一次)
         */
        @Synchronized
        fun evaluate(): Deferred<Throwable?> {
            // 完成后不再执行
            if (isDone) {
                return CompletableDeferred(null)
            } else {
                // 标记所有任务完成
                isDone = true
            }

            return scope.async {

                // 检查前置
                checkDependencies()

                // 触发 ElementEvaluateEvent.Start
                ElementEvaluateEvent.Start(handler, elements).call()

                // 处理元素任务
                try {
                    handler.handle(elements)
                } catch (ex: Throwable) {
                    severe("Failed to handle elements '$elements' by $handler!", ex.stackTraceToString())
                    return@async ex
                }

                // 触发 ElementEvaluateEvent.End
                ElementEvaluateEvent.End(handler).call()

                return@async null
            }
        }

        private suspend fun checkDependencies() {
            for (dependencyClass in config.requires) {
                // 寻找对应类型
                val type = ElementRegistry.findType(dependencyClass.java)
                // 获取其任务组
                val group = evaluations[type]
                // 完成其任务组
                if (group != null) {
                    group.evaluate().join()
                    debug("[EvaluationGroup] Depended handler '${dependencyClass.java}' for '$handler' has been evaluated.")
                }
            }
        }

    }

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