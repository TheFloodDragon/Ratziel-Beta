package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem

/**
 * SimpleItem
 *
 * @author TheFloodDragon
 * @since 2025/5/25 09:04
 */
open class SimpleItem(
    override val data: ItemData = SimpleData(),
) : NeoItem {

    override val service get() = throw UnsupportedOperationException("Service is not supported for SimpleItem!")

    override fun clone() = SimpleItem(this.data.clone())

}