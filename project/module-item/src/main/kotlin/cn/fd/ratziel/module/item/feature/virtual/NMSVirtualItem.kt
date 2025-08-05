package cn.fd.ratziel.module.item.feature.virtual

import taboolib.module.nms.PacketReceiveEvent
import taboolib.module.nms.nmsProxy

/**
 * NMSVirtualItem
 *
 * @author TheFloodDragon
 * @since 2025/8/3 19:36
 */
abstract class NMSVirtualItem {

    abstract fun handleContainerClick(event: PacketReceiveEvent)

    companion object {

        @JvmStatic
        val INSTANCE by lazy { nmsProxy<NMSVirtualItem>() }

    }

}