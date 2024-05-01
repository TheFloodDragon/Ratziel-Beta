package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.element.service.ElementRegistry
import cn.fd.ratziel.core.util.FutureFactory
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.DefaultArgumentFactory
import cn.fd.ratziel.function.argument.SingleArgument
import cn.fd.ratziel.function.argument.popOrNull
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import taboolib.common.platform.function.severe
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
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
        val factory = FutureFactory<Void>()
        // 遍历任务单个评估
        tasks.forEach { task ->
            // 处理函数 (非立即执行)
            val function = Runnable {
                factory += handle(task.handler, task.element) // 提交任务
            }
            // 异步 & 同步 处理
            if (popConfig(task.args).async) {
                CompletableFuture.runAsync(function, executor)
            } else {
                function.run()
            }
        }
        factory.thenRun {
            // 完成后完成传入任务, 返回时间
            future.complete(timeMark.elapsedNow())
        }
    }

    /**
     * 提交评估任务
     */
    fun submitTask(task: EvaluationTask) =
        evaluations.computeIfAbsent(popConfig(task.args).lifeCycle) {
            Collections.synchronizedList(listOf<EvaluationTask>())
        }.add(task)

    /**
     * 解析 [Element] 并提交评估任务
     */
    fun submitWith(element: Element) {
        // 注册到处理器表
        for (handler in ElementRegistry.getHandlersByType(element.type)) {
            val args = DefaultArgumentFactory(SingleArgument(findConfig(handler)))
            submitTask(EvaluationTask(element, handler, args))
        }
    }

    /**
     * 评估任务
     */
    class EvaluationTask(val element: Element, val handler: ElementHandler, val args: ArgumentFactory)

    companion object {

        /**
         * 弹出 [ElementConfig]
         */
        fun popConfig(args: ArgumentFactory) = args.popOrNull<ElementConfig>() ?: ElementConfig()

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

        /**
         * 调用 [ElementHandler] 处理 [Element]
         * @param handler 元素处理器
         * @param element 要处理的元素
         * @return [CompletableFuture] - 过程的中可能存在的异常
         */
        fun handle(handler: ElementHandler, element: Element): CompletableFuture<Void> {
            // 创建评估任务
            val future = CompletableFuture<Void>()
            // 处理元素并完成评估任务
            try {
                handler.handle(element)
            } catch (ex: Throwable) {
                severe("Couldn't handle element $element by $handler")
                ex.printStackTrace()
            }
            return future // 返回任务
        }

    }

}