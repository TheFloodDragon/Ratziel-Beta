package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.module.item.api.trigger.TriggerAction
import cn.fd.ratziel.module.item.api.trigger.TriggerType
import java.util.concurrent.ConcurrentHashMap

/**
 * TriggerMap
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:01
 */
open class TriggerMap(
    protected open val map: MutableMap<TriggerType, TriggerAction> = ConcurrentHashMap(),
) : MutableMap<TriggerType, TriggerAction> by map