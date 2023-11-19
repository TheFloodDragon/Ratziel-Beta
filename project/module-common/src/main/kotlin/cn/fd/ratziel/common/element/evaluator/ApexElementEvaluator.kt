package cn.fd.ratziel.common.element.evaluator

import cn.fd.ratziel.common.event.api.ElementHandleEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.element.service.ElementRegistry
import cn.fd.ratziel.core.util.callThenRun
import taboolib.common.platform.function.severe

/**
 * ApexElementEvaluator
 * 对所有元素处理评估
 *
 * @author TheFloodDragon
 * @since 2023/10/4 12:50
 */
object ApexElementEvaluator {

    /**
     * 基础评估者
     */
    val evaluators: MutableList<ElementEvaluator> = mutableListOf(BasicElementEvaluator)

    /**
     * 对一个元素处理器评估
     * 本质是调用所有评估者评估
     */
    @JvmStatic
    fun eval(handler: ElementHandler, element: Element) = evaluators.forEach { it.eval(handler, element) }

    /**
     * 添加评估者成员
     */
    @JvmStatic
    fun addMember(evaluator: ElementEvaluator) = evaluators.add(evaluator)

    /**
     * 取消评估职位
     */
    @JvmStatic
    fun removeMember(evaluator: ElementEvaluator) = evaluators.remove(evaluator)

    /**
     * 处理一个元素
     */
    @JvmStatic
    fun handleElement(element: Element) =
        ElementRegistry.runWithHandlers(element.type) { (_, handler) ->
            ElementHandleEvent(element, handler).callThenRun { // 事件包装
                try {
                    this.eval(handler, element) // 进行评估
                } catch (e: Exception) {
                    severe("Couldn't handle element $this by $handler")
                    e.printStackTrace()
                }
            }
        }

}