package cn.fd.ratziel.module.item.feature.service

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.service.ItemService
import cn.fd.ratziel.module.item.api.service.ItemServiceRegistry

/**
 * DefaultItemService
 *
 * @author TheFloodDragon
 * @since 2024/5/4 10:52
 */
open class DefaultItemService(
    override val identifier: Identifier,
    val registry: ItemServiceRegistry = GlobalServiceRegistry
) : ItemService {

    /**
     * 使用 [registry]
     */
    override fun <T> get(type: Class<T>): T? = get(type, registry)

    override fun <T> set(type: Class<T>, value: T) = set(type, registry, value)

}