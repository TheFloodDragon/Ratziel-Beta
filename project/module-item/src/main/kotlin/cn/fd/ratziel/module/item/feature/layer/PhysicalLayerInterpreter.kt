package cn.fd.ratziel.module.item.feature.layer

import cn.fd.ratziel.core.functional.MutexedValue
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.SimpleItem
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import cn.fd.ratziel.module.item.impl.builder.provided.ComponentInterpreter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * PhysicalLayerInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/7/24 13:39
 */
object PhysicalLayerInterpreter : ItemInterpreter {

    @JvmField
    val LAYER_ALIAS = arrayOf("layer", "layers")

    init {
        // LAYER_ALIAS 要添加到 accessibleNodes
        DefaultResolver.accessibleNodes.addAll(LAYER_ALIAS)
    }

    override suspend fun interpret(stream: ItemStream) {
        // 获取图层元素
        val property = stream.fetchProperty()
        if (property !is JsonObject || property.isEmpty()) return
        val layerElements = property.getBy(*LAYER_ALIAS)?.jsonObject ?: return

        // 组件解析器
        val interpreter = ItemManager.generatorInterpreter<ComponentInterpreter>(stream.identifier)

        coroutineScope {
            // 序列化图层
            val layers = layerElements.mapValues { entry ->
                async {
                    val layerItem = MutexedValue.initial(SimpleItem())
                    interpreter.parallelSerialize(entry.value, layerItem).joinAll()
                    layerItem.withValue { it.data.tag }
                }
            }.mapValues { PhysicalLayer(it.key, it.value.await()) }
            // 写入到物品数据
            stream.item.useValue {
                PhysicalLayer.writeLayers(data, layers)
            }
        }
    }

}