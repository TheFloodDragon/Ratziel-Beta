@file:OptIn(ExperimentalCoroutinesApi::class)

package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.elementAlias
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.core.serialization.getElementNames
import cn.fd.ratziel.core.serialization.handle
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.ItemKSerializer
import cn.fd.ratziel.module.item.impl.builder.DefaultItemSerializer.json
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.module.item.impl.component.serializers.ItemMaterialSerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.json.*

/**
 * CommonItemSerializer
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:58
 */
object CommonItemSerializer : ItemKSerializer<ItemMetadata> {

    /**
     * 使用到的节点
     */
    val usedNodes by lazy { serializers.flatMap { it.descriptor.elementAlias }.toTypedArray() }

    /**
     * 使用到的序列化器列表
     */
    val serializers = arrayOf(
        ItemMetadata.serializer(),
        ItemDisplay.serializer(),
        ItemDurability.serializer(),
        ItemSundry.serializer(),
        ItemCharacteristic.serializer(),
    )

    override val descriptor = ItemMetadata.serializer().descriptor

    /**
     * 反序列化 (检查结构化解析)
     */
    override fun deserialize(element: JsonElement): ItemMetadata {
        // 结构化解析
        if (isStructured(element)) return json.decodeFromJsonElement(ItemMetadata.serializer(), element)
        val deferred = ItemElement.buildScope.async {
            // 一般解析
            val display = async { json.decodeFromJsonElement(ItemDisplay.serializer(), element) }
            val durability = async { json.decodeFromJsonElement(ItemDurability.serializer(), element) }
            val sundry = async { json.decodeFromJsonElement(ItemSundry.serializer(), element) }
            val characteristic = async { json.decodeFromJsonElement(ItemCharacteristic.serializer(), element) }
            val material = async {
                val name = (element as? JsonObject)?.getBy(NODES_MATERIAL)
                if (name != null) json.decodeFromJsonElement(ItemMaterialSerializer, name) else ItemMaterial.EMPTY
            }
            ItemMetadata(material.await(), display.await(), durability.await(), sundry.await(), characteristic.await())
        }
        return deferred.asCompletableFuture().get()
    }

    /**
     * 序列化 (强制开启结构化解析)
     */
    override fun serialize(component: ItemMetadata) = forceStructured(json.encodeToJsonElement(ItemMetadata.serializer(), component))

    val NODES_MATERIAL = ItemMetadata.serializer().descriptor.getElementNames(ItemMetadata::material.name)

    /**
     * 结构化解析
     */
    const val NODE_STRUCTURED = "structured"

    private fun isStructured(element: JsonElement): Boolean = try {
        element.jsonObject[NODE_STRUCTURED]!!.jsonPrimitive.boolean
    } catch (_: Exception) {
        false
    }

    private fun forceStructured(element: JsonElement): JsonElement = try {
        element.jsonObject.handle { put(NODE_STRUCTURED, JsonPrimitive(true)) }
    } catch (_: Exception) {
        element
    }

}