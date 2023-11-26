package cn.fd.ratziel.common.element.evaluator

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.function.FutureFactory
import taboolib.common.platform.function.postpone
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

/**
 * BasicElementEvaluator - 基本元素处理评估者
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:03
 */
object BasicElementEvaluator : ElementEvaluator {

    internal val futures = FutureFactory<Long>()

    override fun eval(handler: ElementHandler, element: Element) {
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
        // 提交任务
        val future = CompletableFuture<Long>().also { futures += it }
        // 推送任务
        postpone(config.lifeCycle) {
            measureTimeMillis {
                handler.handle(element) // 处理
            }.let { future.complete(it) }
        }
    }

}