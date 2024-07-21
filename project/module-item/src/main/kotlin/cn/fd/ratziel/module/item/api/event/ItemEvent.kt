package cn.fd.ratziel.module.item.event

import cn.fd.ratziel.core.Identifier
import taboolib.platform.type.BukkitProxyEvent

/**
 * ItemEvent
 *
 * @author TheFloodDragon
 * @since 2024/7/3 17:22
 */
open class ItemEvent(
    /**
     * 物品标识符
     */
    val identifier: Identifier,
) : BukkitProxyEvent() {
    override val allowCancelled = false
}