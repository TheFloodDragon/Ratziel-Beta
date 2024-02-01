package cn.fd.ratziel.core.element

import cn.fd.ratziel.core.element.api.ElementHandler

/**
 * ExtElementHandler - 扩展元素处理器
 *
 * @author TheFloodDragon
 * @since 2024/2/1 10:53
 */
interface ExtElementHandler : ElementHandler {

    /**
     * 在元素开始加载时调用
     */
    fun onStart() {}

    /**
     * 在元素加载和评估任务完成时调用
     */
    fun onFinish() {}

}