@file:OptIn(ExperimentalCoroutinesApi::class)

package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.*
import cn.fd.ratziel.core.serialization.serializers.UUIDSerializer
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.ItemKSerializer
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.module.item.impl.component.serializers.*
import cn.fd.ratziel.module.nbt.NBTData
import cn.fd.ratziel.module.nbt.NBTSerializer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import java.util.*

/**
 * CommonItemSerializer
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:58
 */
object DefaultSerializer : ItemKSerializer<ItemMetadata> {

    val json = Json(baseJson) {
        serializersModule += SerializersModule {
            // Basic Serializers
            contextual(UUID::class, UUIDSerializer)
            // Common Serializers
            contextual(NBTData::class, NBTSerializer)
            contextual(ItemMaterial::class, ItemMaterialSerializer)
            // Bukkit Serializers
            contextual(Enchantment::class, EnchantmentSerializer)
            contextual(HideFlag::class, HideFlagSerializer)
            contextual(Attribute::class, AttributeSerializer)
            contextual(AttributeModifier::class, AttributeModifierSerializer)
        }
    }

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
        val deferred = ItemElement.scope.async {
            // 获取材料 (需要优先获取)
            val materialName = (element as? JsonObject)?.getBy(NODES_MATERIAL)
            val material = if (materialName != null) json.decodeFromJsonElement(ItemMaterialSerializer, materialName) else ItemMaterial.EMPTY
            // 一般解析
            val display = async { json.decodeFromJsonElement(ItemDisplay.serializer(), element) }
            val durability = async { json.decodeFromJsonElement(ItemDurability.serializer(), element) }
            val sundry = async { json.decodeFromJsonElement(ItemSundry.serializer(), element) }
            val characteristic = async { json.decodeFromJsonElement(ItemCharacteristic.serializer(), element) }
            ItemMetadata(material, display.await(), durability.await(), sundry.await(), characteristic.await())
        }
        return deferred.asCompletableFuture().get()
    }

    /**
     * 序列化 (强制开启结构化解析)
     */
    override fun serialize(component: ItemMetadata) = forceStructured(json.encodeToJsonElement(ItemMetadata.serializer(), component))

    @Deprecated("Will be removed")
    val NODES_MATERIAL = ItemMetadata.serializer().descriptor.getElementNames(ItemMetadata::material.name)

    /**
     * 结构化解析
     */
    @Deprecated("Will be removed")
    const val NODE_STRUCTURED = "structured"

    @Deprecated("Will be removed")
    private fun isStructured(element: JsonElement): Boolean = try {
        element.jsonObject[NODE_STRUCTURED]!!.jsonPrimitive.boolean
    } catch (_: Exception) {
        false
    }

    @Deprecated("Will be removed")
    private fun forceStructured(element: JsonElement): JsonElement = try {
        element.jsonObject.handle { put(NODE_STRUCTURED, JsonPrimitive(true)) }
    } catch (_: Exception) {
        element
    }

    /**
     * 通过 [KSerializer] 创建一个 [ItemSerializer]
     */
    fun <T> createItemSerializer(serializer: KSerializer<T>): ItemSerializer<T> = DelegateItemSerializer(DefaultSerializer.json, serializer)

    /**
     * 托管 [KSerializer] 以实现 [ItemSerializer]
     */
    private class DelegateItemSerializer<T>(val json: Json, val serializer: KSerializer<T>) : ItemSerializer<T>, KSerializer<T> by serializer {
        override fun serialize(component: T) = json.encodeToJsonElement(serializer, component)
        override fun deserialize(element: JsonElement) = json.decodeFromJsonElement(serializer, element)
    }

}