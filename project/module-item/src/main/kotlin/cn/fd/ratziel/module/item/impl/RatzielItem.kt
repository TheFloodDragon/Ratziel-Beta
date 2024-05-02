package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemService
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.service.RatzielItemService
import java.util.*

/**
 * RatzielItem
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:05
 */
open class RatzielItem(
    /**
     * 物品数据
     */
    override val data: ItemData,
    /**
     * 物品唯一标识符
     */
    open val uuid: UUID = UUID.randomUUID(),
) : NeoItem {

    override val service: ItemService by lazy { RatzielItemService(this) }

}