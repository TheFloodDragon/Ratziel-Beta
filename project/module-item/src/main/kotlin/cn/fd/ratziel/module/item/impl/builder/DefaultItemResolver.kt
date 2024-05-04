package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.asMutable
import cn.fd.ratziel.core.serialization.handle
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * DefaultItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/4/20 10:03
 */
class DefaultItemResolver : ItemResolver {

    override fun resolve(element: JsonElement, arguments: ArgumentFactory) = access(element) { origin ->
        // 过滤拿到处理对象并处理
        val handle = origin.filter { DefaultItemSerializer.occupiedNodes.contains(it.key) }.asMutable()
        // 写回修改过的部分
        putAll(handle)
    }

    fun access(target: JsonElement, action: MutableMap<String, JsonElement>.(JsonObject) -> Unit) =
        if (target is JsonObject) target.handle(action) else target

}