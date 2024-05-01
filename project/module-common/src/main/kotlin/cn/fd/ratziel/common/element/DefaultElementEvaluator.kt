package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.element.service.ElementRegistry
import cn.fd.ratziel.core.util.FutureFactory
import taboolib.common.LifeCycle
import taboolib.common.TabooLib
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import kotlin.time.TimeSource
import kotlin.time.measureTime
import kotlin.time.toJavaDuration
import java.time.Duration as JavaDuration

/**
 * DefaultElementEvaluator - 元素评估器
 *
 * @author TheFloodDragon
 * @since 2024/5/1 11:42
 */
class DefaultElementEvaluator(val executor: Executor) : ElementEvaluator {

    val handlerMap: MutableMap<Element, MutableMap<ElementHandler, ElementConfig>> = ConcurrentHashMap()

    /**
     * 对一个元素处理器评估
     * 本质是调用所有评估者评估
     */
    override fun eval(handler: ElementHandler, element: Element): CompletableFuture<Throwable?> {
        // 创建评估任务
        val future = CompletableFuture<Throwable?>()
        // 处理元素并完成评估任务
        try {
            handler.handle(element)
            future.complete(null)
        } catch (ex: Throwable) {
            future.complete(ex)
        }
        return future // 返回任务
    }

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

    fun evaluate(): CompletableFuture<JavaDuration> {
        val out = mutableListOf<CompletableFuture<JavaDuration>>()
        for (lifeCycle in arrayOf(LifeCycle.LOAD, LifeCycle.ENABLE, LifeCycle.ACTIVE)) {
            TabooLib.registerLifeCycleTask(lifeCycle, 10) {
                // 时间标记
                val timeMark = TimeSource.Monotonic.markNow()
                // FutureFactory
                val factory = FutureFactory<Throwable?>()
                for (entry in handlerMap) {
                    measureTime { }
                    val element = entry.key
                    // 获取到对应生命周期的处理任务
                    val tasks = entry.value.filter { it.value.lifeCycle == lifeCycle }
                    tasks.forEach { (handler, config) ->
                        // 创建评估任务并提交
                        val future = CompletableFuture<Throwable?>().also { factory += it }
                        // 处理函数 (非立即执行)
                        val function = Runnable {
                            // 处理元素并完成评估任务
                            try {
                                handler.handle(element)
                                future.complete(null)
                            } catch (ex: Throwable) {
                                future.complete(ex)
                            }
                        }
                        // 异步 & 同步 处理
                        if (config.async) {
                            CompletableFuture.runAsync(function, executor)
                        } else {
                            function.run()
                        }
                    }
                }
                out.add(factory.thenApply { timeMark.elapsedNow().toJavaDuration() })
            }
        }
        return out.toTypedArray().let { futures ->
            CompletableFuture.allOf(*futures).thenApply { _ ->
                var duration: JavaDuration = JavaDuration.ZERO
                futures.forEach {
                    duration += it.getNow(JavaDuration.ZERO)
                }
                return@thenApply duration
            }
        }
    }

    /**
     * 处理元素
     */
    fun submitElement(element: Element) {
        handlerMap[element] = ConcurrentHashMap<ElementHandler, ElementConfig>().apply {
            // 注册到处理器表
            for (handler in ElementRegistry.getHandlersByType(element.type)) {
                put(handler, findConfig(handler))
            }
        }
    }

}