package cn.fd.ratziel.module.item.internal.compat

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.impl.SimpleItem

/**
 * CompatItem
 *
 * @author TheFloodDragon
 * @since 2025/4/4 15:48
 */
open class CompatItem(
    val name: String,
    data: ItemData,
) : SimpleItem(data)