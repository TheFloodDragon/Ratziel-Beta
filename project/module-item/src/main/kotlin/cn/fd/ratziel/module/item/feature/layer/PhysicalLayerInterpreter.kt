package cn.fd.ratziel.module.item.feature.layer

import cn.fd.ratziel.core.functional.SynchronizedValue
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import cn.fd.ratziel.module.item.impl.builder.provided.ComponentInterpreter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.serialization.json.JsonObject

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
        val element = stream.fetchElement()
        if (element !is JsonObject) return
        val layerElements = element.getBy(*LAYER_ALIAS) as? JsonObject ?: return

        coroutineScope {
            // 序列化图层
            val layers = layerElements.mapValues { entry ->
                async {
                    SynchronizedValue.initial(SimpleData()).also { data ->
                        ComponentInterpreter.parallelSerialize(entry.value, data).joinAll()
                    }.withValue { it.tag }
                }
            }.mapValues { PhysicalLayer(it.key, it.value.await()) }
            // 写入到物品数据
            stream.data.withValue {
                PhysicalLayer.writeLayers(it, layers)
            }
        }
    }

}