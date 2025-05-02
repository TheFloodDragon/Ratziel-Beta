package cn.fd.ratziel.module.item.api.action

/**
 * ActionMap - 物品动作表
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:22
 */
class ActionMap(
    private val map: Map<ItemTrigger, ItemAction>,
) : Map<ItemTrigger, ItemAction> by map