package cn.fd.ratziel.module.item.api

import cn.fd.ratziel.core.Identifier

/**
 * IdentifiedItem
 *
 * @author TheFloodDragon
 * @since 2025/7/6 18:03
 */
interface IdentifiedItem : NeoItem {

    /**
     * 物品标识符
     */
    val identifier: Identifier

    /**
     * 克隆 [IdentifiedItem] 实例
     */
    override fun clone(): IdentifiedItem

}