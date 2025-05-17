package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.nbt.NbtAdapter
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import java.util.concurrent.ConcurrentHashMap

/**
 * DefinitionInterceptor
 *
 * @author TheFloodDragon
 * @since 2025/5/10 19:49
 */
@AutoRegister
object DefinitionInterceptor : ItemInterceptor {

    override suspend fun intercept(scope: CoroutineScope, stream: ItemStream) {
        val element = stream.fetchElement()
        if (element !is JsonObject) return

        // 获取定义上下文 TODO
        val definition: MutableMap<String, Any?> = ConcurrentHashMap()

        // 定义处理
        val defineTask = scope.launch {
            val define = element["define"] as? JsonObject ?: return@launch
            for ((name, script) in define) {
                val result = ScriptBlockBuilder.build(script).execute(context) ?: continue
                // 扔到 definition 中
                definition[name] = result
            }
        }

        // 数据处理
        val dataTask = scope.launch {
            val data = element["data"] as? JsonObject ?: return@launch

            // 创建数据容器
            val holder = RatzielItem.Holder(SimpleData())

            for ((name, script) in data) {
                val result = ScriptBlockBuilder.build(script).execute(stream.context)
                // 扔到定义中
                definition[name] = result
                // 处理 Nbt 数据
                val tag = (result ?: continue) as? NbtTag
                    ?: runCatching { NbtAdapter.box(result) }.getOrNull() ?: continue
                // 扔到数据容器中
                holder[name] = tag
            }

            // 写入数据
            stream.data.withValue {
                it.tag.merge(holder.data.tag, true)
            }
        }

        // 等待所有任务完成
        defineTask.join()
        dataTask.join()
    }

}