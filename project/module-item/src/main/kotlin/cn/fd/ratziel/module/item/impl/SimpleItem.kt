package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.service.ItemService

/**
 * SimpleItem
 *
 * @author TheFloodDragon
 * @since 2025/5/25 09:04
 */
open class SimpleItem(override val data: ItemData) : NeoItem {

    override val service: ItemService
        get() = throw UnsupportedOperationException("Service is not supported for SimpleItem!")

}