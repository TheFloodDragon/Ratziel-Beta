package cn.fd.ratziel.core.element.event

import taboolib.common.platform.event.ProxyEvent

/**
 * ElementTypeMatchEvent
 * 匹配元素事件
 *
 * @author TheFloodDragon
 * @since 2023/9/2 10:48
 */
class ElementTypeMatchEvent(
    /**
     * 匹配源表达式
     */
    var source: String,
) : ProxyEvent()