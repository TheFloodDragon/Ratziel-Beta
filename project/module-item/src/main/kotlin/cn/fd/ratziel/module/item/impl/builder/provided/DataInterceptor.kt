package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.builder.provided.DefinitionInterceptor.definition
import cn.fd.ratziel.module.nbt.NbtAdapter
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * DataInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/11 10:21
 */
@AutoRegister
object DataInterceptor : ItemInterceptor {

    override fun intercept(identifier: Identifier, element: JsonElement, context: ArgumentContext): ItemData? {
        if (element !is JsonObject) return null
        val data = element["data"] as? JsonObject ?: return null

        // 获取定义上下文
        val definition = context.definition()
        // 创建数据容器
        val holder = RatzielItem.Holder(SimpleData())

        for ((name, script) in data) {
            val result = ScriptBlockBuilder.build(script).execute(context)
            // 扔到定义中
            definition[name] = result
            // 处理 Nbt 数据
            val tag = (result ?: continue) as? NbtTag
                ?: runCatching { NbtAdapter.box(result) }.getOrNull() ?: continue
            // 扔到数据容器中
            holder[name] = tag
        }

        // 返回容器中的数据
        return holder.data
    }

}