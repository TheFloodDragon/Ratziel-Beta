package cn.fd.ratziel.common.event

import taboolib.common.platform.event.ProxyEvent

/**
 * ElementTypeMatchEvent - 元素类型匹配事件
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