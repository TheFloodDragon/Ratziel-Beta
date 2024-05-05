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
open class RatzielItem : NeoItem {

    constructor(identifier: ItemIdentifier, data: ItemData) {
        this.identifier = identifier
        this.data = data
    }

    constructor(data: ItemData) : this(ItemIdentifierImpl.random(), data)

    constructor() : this(ItemDataImpl())

    /**
     * 物品唯一标识符
     */
    val identifier: ItemIdentifier

    /**
     * 物品数据
     */
    final override var data: ItemData
        protected set

    /**
     * 物品服务
     */
    override val service get() = GlobalServiceManager[identifier]

}