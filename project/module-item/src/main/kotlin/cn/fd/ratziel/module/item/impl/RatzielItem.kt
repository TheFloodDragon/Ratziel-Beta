package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemIdentifier
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.service.ServiceManager

/**
 * RatzielItem
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:05
 */
class RatzielItem(
    /**
     * 物品唯一标识符
     */
    val id: ItemIdentifier = ItemIdentifierImpl.random(),
) : NeoItem {

    constructor(id: ItemIdentifier, data: ItemData) : this(id) {
        this.data = data
    }

    /**
     * 物品数据
     */
    override var data = ItemData()
        private set

    /**
     * 物品服务
     */
    override val service get() = ServiceManager.services[id]

}