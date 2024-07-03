package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.element.service.ElementRegistry
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.function.argument.DefaultArgumentContext
import cn.fd.ratziel.function.argument.popOrNull
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * ElementEvaluator - 元素评估器
 *
 * @author TheFloodDragon
 * @since 2024/5/1 11:42
 */
class ElementEvaluator(val executor: Executor) {

    val evaluations: MutableMap<LifeCycle, MutableList<EvaluationTask>> = ConcurrentHashMap()

    /**
     * 开始评估
     */
    fun evaluate(): CompletableFuture<Duration> {
        val cycleTasks = FutureFactory<Duration>()
        // 遍历生命周期创建周期任务
        for ((lifeCycle, tasks) in evaluations.entries) {
            // 创建周期任务回调
            val future = CompletableFuture<Duration>().also { cycleTasks.add(it) }
            // 注册周期任务
            TabooLib.registerLifeCycleTask(lifeCycle, 10) { handleCycle(tasks, future) }
        }
        return cycleTasks.thenApply { durations ->
            // 合并时间
            var duration = Duration.ZERO
            durations.forEach { duration = duration.plus(it) }
            duration
        }
    }

    fun handleCycle(tasks: List<EvaluationTask>, future: CompletableFuture<Duration>) {
        // 开始记录时间
        val timeMark = TimeSource.Monotonic.markNow()
        // 创建工厂收集任务
        val factory = FutureFactory<Throwable?>()
        // 遍历任务单个处理
        tasks.forEach { task ->
            factory += handleElement(task.handler, task.element) // 提交任务
        }
        factory.thenRun {
            // 完成后完成传入任务, 返回时间
            future.complete(timeMark.elapsedNow())
        }
    }

    /**
     * 调用 [ElementHandler] 处理 [Element]
     * @param handler 元素处理器
     * @param element 要处理的元素
     * @return [CompletableFuture] - 过程的中可能存在的异常
     */
    fun handleElement(handler: ElementHandler, element: Element, async: Boolean = false): CompletableFuture<Throwable?> {
        // 创建处理任务
        val future = CompletableFuture<Throwable?>()
        // 处理函数 (非立即执行)
        val function = Runnable {
            try {
                handler.handle(element)
                future.complete(null) // 完成任务
            } catch (ex: Throwable) {
                severe("Couldn't handle element by $handler!")
                ex.printStackTrace()
                future.complete(ex) // 异常时返回(尽管已经处理过了)
            }
        }
        // 异步 & 同步 处理
        if (async) {
            CompletableFuture.runAsync(function, executor)
        } else {
            function.run()
        }
        return future // 返回任务
    }

    /**
     * 提交评估任务
     */
    fun submitTask(task: EvaluationTask) =
        evaluations.computeIfAbsent(popConfig(task.args).lifeCycle) { CopyOnWriteArrayList() }.add(task)

    /**
     * 解析 [Element] 并提交评估任务
     */
    fun submitWith(element: Element) {
        // 注册到处理器表
        for (handler in ElementRegistry.getHandlersByType(element.type)) {
            val args = DefaultArgumentContext(findConfig(handler))
            submitTask(EvaluationTask(element, handler, args))
        }
    }

    /**
     * 评估任务
     */
    class EvaluationTask(val element: Element, val handler: ElementHandler, val args: ArgumentContext)

    companion object {

        /**
         * 弹出 [ElementConfig]
         */
        fun popConfig(args: ArgumentContext): ElementConfig = args.popOrNull<ElementConfig>() ?: ElementConfig()

        /**
         * 获取[ElementHandler]的[ElementConfig]
         */
        fun findConfig(handler: ElementHandler): ElementConfig {
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

}