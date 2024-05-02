package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.module.item.api.ItemService
import cn.fd.ratziel.module.item.impl.RatzielItem

/**
 * RatzielItemService
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:04
 */
class RatzielItemService(
    val item: RatzielItem
) : ItemService {

    override fun <T> get(type: Class<T>): T {
        TODO("Not yet implemented")
    }

}