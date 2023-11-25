package cn.fd.ratziel.common.element.evaluator

import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler
import taboolib.common.platform.function.postpone
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor

/**
 * BasicElementEvaluator
 * 基本元素处理评估者
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:03
 */
object BasicElementEvaluator : ElementEvaluator {

    override fun eval(handler: ElementHandler, element: Element) {
        /*
         * 分析注解
         */
        val handlerClass = handler::class.java
        val annoClass = ElementConfig::class.java
        val method = handlerClass.getMethod("handle", Element::class.java)
        var config = ElementConfig()
        // 处理函数有插件生命周期注解
        if (method.isAnnotationPresent(annoClass))
            config = method.getAnnotation(annoClass)
        // 类有插件生命周期注解
        else if (handlerClass.isAnnotationPresent(annoClass))
            config = handlerClass.getAnnotation(annoClass)
        // 处理
        postpone(config.lifeCycle) {
            // 处理函数 (非立即执行)
            val function: PlatformExecutor.PlatformTask.() -> Unit = { handler.handle(element) }
            // 异步或同步执行
            submit(now = true, async = !config.sync, executor = function)
        }
    }

}