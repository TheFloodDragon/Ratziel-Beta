package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.serialization.MutableJsonObject
import cn.fd.ratziel.core.serialization.asMutable
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.api.ArgumentResolver
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * ItemSectionResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/24 20:53
 */
interface ItemSectionResolver : ItemResolver {

    fun resolveWith(builder: MutableJsonObject, arguments: ArgumentFactory): JsonElement

    fun resolveWith(element: JsonElement, arguments: ArgumentFactory): JsonElement

    override fun resolve(element: JsonElement, arguments: ArgumentFactory): JsonElement =
        if (element is JsonObject) resolveWith(element.asMutable(), arguments)
        else resolveWith(element, arguments)

    interface TagResolver : ArgumentResolver<Iterable<String>, String?> {

        /**
         * 解析器名称
         */
        val name: String

        /**
         * 解析器别名
         */
        val alias: Array<String>

    }

}