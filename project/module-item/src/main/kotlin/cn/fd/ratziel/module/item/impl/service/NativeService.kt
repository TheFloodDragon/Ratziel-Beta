package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.function.argument.Argument
import cn.fd.ratziel.module.item.api.ItemService
import cn.fd.ratziel.module.item.impl.RatzielItem

/**
 * NativeService
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:04
 */
class NativeService(
    val item: RatzielItem
) : ItemService {

    override fun <T : Any> get(type: Class<T>): Argument<T> {
        TODO("Not yet implemented")
    }

}