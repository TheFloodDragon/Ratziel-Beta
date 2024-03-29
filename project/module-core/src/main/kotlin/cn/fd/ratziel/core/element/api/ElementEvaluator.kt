package cn.fd.ratziel.core.element.api

import cn.fd.ratziel.core.element.Element
import java.util.concurrent.CompletableFuture

/**
 * ElementEvaluator
 *
 * @author TheFloodDragon
 * @since 2023/10/4 12:47
 */
interface ElementEvaluator {

    /**
     * 评估元素处理器对元素的处理
     * @param handler 元素处理器
     * @param element 要处理的元素
     * @return 评估结果 (由[CompletableFuture]包装)
     */
    fun eval(handler: ElementHandler, element: Element)

}