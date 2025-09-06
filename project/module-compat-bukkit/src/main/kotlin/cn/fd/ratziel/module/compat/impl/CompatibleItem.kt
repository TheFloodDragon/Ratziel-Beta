package cn.fd.ratziel.module.compat.impl

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.impl.SimpleItem

/**
 * CompatItem
 *
 * @author TheFloodDragon
 * @since 2025/4/4 15:48
 */
open class CompatibleItem(
    val pluginName: String,
    data: ItemData,
) : SimpleItem(data)