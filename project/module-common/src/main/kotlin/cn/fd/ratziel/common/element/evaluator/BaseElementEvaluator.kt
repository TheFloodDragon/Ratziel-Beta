package cn.fd.ratziel.common.element.evaluator

import cn.fd.ratziel.common.annotation.OnLifeCycle
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler
import taboolib.common.platform.function.postpone

/**
 * BaseElementEvaluator
 * 基础元素处理评估者
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:03
 */
object BaseElementEvaluator : ElementEvaluator {

    override fun eval(handler: ElementHandler, element: Element) {
        // 处理函数 (非立即执行)
        val runnable = Runnable { handler.handle(element) }
        /*
         * 分析注解
         */
        val clazz = handler::class.java
        val anno = OnLifeCycle::class.java
        val method = clazz.getMethod("handle", Element::class.java)
        // 处理函数有插件生命周期注解
        if (method.isAnnotationPresent(anno))
            postpone(method.getAnnotation(anno).cycle, runnable)
        // 类有插件生命周期注解
        else if (clazz.isAnnotationPresent(anno))
            postpone(clazz.getAnnotation(anno).cycle, runnable)
        else runnable.run() // 直接处理
    }

}