package cn.fd.ratziel.common.event.api

import cn.fd.ratziel.core.element.Element

/**
 * ElementEvent
 * 有关元素的事件
 *
 * @author TheFloodDragon
 * @since 2023/9/2 10:28
 */
interface ElementEvent {

    /**
     * 操作的元素
     */
    var element: Element

}