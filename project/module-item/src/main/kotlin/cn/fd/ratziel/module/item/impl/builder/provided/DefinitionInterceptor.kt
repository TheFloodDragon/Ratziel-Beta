package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.nbt.NbtAdapter
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.serialization.json.JsonObject

/**
 * DefinitionInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 19:49
 */
object DefinitionInterceptor : ItemInterceptor {

    override fun intercept(element: Element, context: ArgumentContext): ItemData? {
        val property = element.property as? JsonObject ?: return null
        val define = property["define"] as? JsonObject ?: return null
        val holder = RatzielItem.Holder(SimpleData())

        for ((name, script) in define) {
            val result = ScriptBlockBuilder.build(script).execute(context) ?: continue
            val tag = result as? NbtTag ?: runCatching { NbtAdapter.box(result) }.getOrNull() ?: continue
            holder[name] = tag
        }

        return holder.data
    }

}