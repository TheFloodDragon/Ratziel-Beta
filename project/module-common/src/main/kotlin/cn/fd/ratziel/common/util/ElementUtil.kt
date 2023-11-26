package cn.fd.ratziel.common.util

import cn.fd.ratziel.common.element.evaluator.ApexElementEvaluator
import cn.fd.ratziel.common.element.evaluator.BasicElementEvaluator.futures
import cn.fd.ratziel.core.element.Element
import kotlin.system.measureTimeMillis

/**
 * 处理元素 (计时)
 */
fun Element.handle(): Long = measureTimeMillis {
    futures.clear() // 清空缓存
    ApexElementEvaluator.handleElement(this) // 处理元素
}.let { it + measureTimeMillis { futures.waitForAll() } }