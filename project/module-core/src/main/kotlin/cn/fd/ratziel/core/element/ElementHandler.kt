package cn.fd.ratziel.core.element

/**
 * ElementHandler - 元素处理器
 *
 * @author TheFloodDragon
 * @since 2024/4/21 9:46
 */
interface ElementHandler {

    /**
     * 处理单个元素
     */
    fun handle(element: Element)

    /**
     * 在元素加载前调用
     *
     * @param elements 交给该 [ElementHandler] 解析的所有元素
     */
    fun onStart(elements: Collection<Element>) {}

    /**
     * 在元素加载后调用
     */
    fun onEnd() {}

}