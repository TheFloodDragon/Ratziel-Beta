package cn.fd.ratziel.module.ui.inventory.internal.nms

import taboolib.module.nms.nmsProxy

/**
 * InventoryHandler
 *
 * @author TheFloodDragon
 * @since 2025/10/4 20:43
 */
abstract class InventoryHandler {

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            nmsProxy<InventoryHandler>()
        }

    }

}