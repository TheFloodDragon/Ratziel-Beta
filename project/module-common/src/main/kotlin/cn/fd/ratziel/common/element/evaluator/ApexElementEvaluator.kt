package cn.fd.ratziel.common.element.evaluator

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementEvaluator
import cn.fd.ratziel.core.element.api.ElementHandler

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
    val evaluators: MutableList<ElementEvaluator> = mutableListOf(BaseElementEvaluator)

    /**
     * 对一个元素处理器评估
     * 本质是调用所有评估者评估
     */
    fun eval(handler: ElementHandler, element: Element) = evaluators.forEach { it.eval(handler, element) }

    /**
     * 添加评估者成员
     */
    fun addMember(evaluator: ElementEvaluator) = evaluators.add(evaluator)

    /**
     * 取消评估职位
     */
    fun removeMember(evaluator: ElementEvaluator) = evaluators.remove(evaluator)

}