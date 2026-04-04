package cn.fd.ratziel.module.item.api.component

import cn.fd.ratziel.module.item.api.ItemData

/**
 * ItemComponentData - 支持组件读写的物品数据
 *
 * @author TheFloodDragon
 * @since 2026/3/22 16:26
 */
interface ItemComponentData : ItemData, ItemComponentHolder {

    /**
     * 克隆数据
     */
    override fun clone(): ItemComponentData

}
