package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.serialization.json.JsonElement

/**
 * DefaultItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/4/20 10:03
 */
class DefaultItemResolver : ItemResolver {

    override fun resolve(target: JsonElement): JsonElement {
        return target
    }

}