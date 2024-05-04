package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemIdentifier
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.service.GlobalServiceManager

/**
 * RatzielItem
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:05
 */
open class RatzielItem(
    /**
     * 物品唯一标识符
     */
    val identifier: ItemIdentifier,
) : NeoItem {

    constructor() : this(ItemIdentifierImpl.random())

    constructor(data: ItemData) : this(ItemIdentifierImpl.random(), data)

    constructor(id: ItemIdentifier, data: ItemData) : this(id) {
        this.data = data
    }

    /**
     * 物品数据
     */
    final override var data = ItemData()
        protected set

    /**
     * 物品服务
     */
    override val service get() = GlobalServiceManager[identifier]

}