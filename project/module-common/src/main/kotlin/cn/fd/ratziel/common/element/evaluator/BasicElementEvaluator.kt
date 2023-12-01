package cn.fd.ratziel.common.element.evaluator

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.function.futureRunAsync
import taboolib.common.platform.function.postpone
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * BasicElementEvaluator - 基本元素处理评估者
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:03
 */
object BasicElementEvaluator : ElementEvaluator {

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
        // 创建评估任务并提交
        val future = CompletableFuture<Duration>().also { ApexElementEvaluator.evalTasks += it }
        // 函数 (非立即执行) - 处理元素并完成评估任务
        val function = Runnable { measureTime { handler.handle(element) }.let { future.complete(it) } }
        // 推迟加载
        postpone(config.lifeCycle) {
            // 异步同步处理
            if (config.async) futureRunAsync(function)
            else function.run()
        }

    }

}