package cn.fd.ratziel.module.item.impl.component

/**
 * ItemComponentMap
 * 
 * @author TheFloodDragon
 * @since 2025/11/22 21:54
 */
class ItemComponentMap(content: MutableMap<NamespacedIdentifier, ItemComponentData>) : MutableMap<NamespacedIdentifier, ItemComponentData> by content {

    fun restore(type: NamespacedIdentifier) {
        this[type] = ItemComponentData.removed()
    }

}