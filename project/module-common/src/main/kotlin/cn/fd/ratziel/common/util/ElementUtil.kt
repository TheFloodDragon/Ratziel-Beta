package cn.fd.ratziel.common.util

import cn.fd.ratziel.common.element.evaluator.ApexElementEvaluator
import cn.fd.ratziel.core.element.Element

/**
 * 处理元素
 */
fun Element.handle() = ApexElementEvaluator.handleElement(this)