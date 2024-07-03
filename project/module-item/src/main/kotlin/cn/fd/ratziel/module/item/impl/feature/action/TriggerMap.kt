package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import java.util.concurrent.ConcurrentHashMap

/**
 * TriggerMap
 *
 * @author TheFloodDragon
 * @since 2024/7/3 18:53
 */
open class TriggerMap(
    protected open val map: MutableMap<ItemTrigger, ItemAction> = ConcurrentHashMap(),
) : MutableMap<ItemTrigger, ItemAction> by map