package cn.fd.ratziel.module.item.internal.compat

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.service.ItemService

/**
 * CompatItem
 *
 * @author TheFloodDragon
 * @since 2025/4/4 15:48
 */
open class CompatItem(
    val name: String,
    override val data: ItemData
) : NeoItem {

    override val service: ItemService
        get() = throw UnsupportedOperationException("Service is not supported for $name!")

}