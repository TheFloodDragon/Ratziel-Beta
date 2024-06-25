package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.*
import cn.fd.ratziel.core.serialization.serializers.UUIDSerializer
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.ItemKSerializer
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.module.item.impl.component.serializers.*
import cn.fd.ratziel.module.item.nbt.NBTData
import cn.fd.ratziel.module.item.nbt.NBTSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemSerializer - 通过 [Json] 序列化/反序列化 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:58
 */
object DefaultItemSerializer : ItemKSerializer<ItemMetadata> {

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
     * 使用到的序列化器列表
     */
    val serializers = arrayOf(
        ItemMetadata.serializer(),
        ItemDisplay.serializer(),
        ItemDurability.serializer(),
        ItemSundry.serializer(),
    )

    override val descriptor = ItemMetadata.serializer().descriptor

    /**
     * 反序列化 (检查结构化解析)
     */
    override fun deserialize(element: JsonElement): ItemMetadata {
        // 结构化解析
        if (isStructured(element)) return json.decodeFromJsonElement(ItemMetadata.serializer(), element)
        // 异步方法
        fun <T> asyncDecode(deserializer: DeserializationStrategy<T>, from: JsonElement = element) =
            CompletableFuture.supplyAsync({
                json.decodeFromJsonElement(deserializer, from)
            }, ItemElement.executor).exceptionally { it.printStackTrace();null }
        // 一般解析
        val display = asyncDecode(ItemDisplay.serializer())
        val durability = asyncDecode(ItemDurability.serializer())
        val sundry = asyncDecode(ItemSundry.serializer())
        val material = (element as? JsonObject)?.get(NODES_MATERIAL)
            ?.let { asyncDecode(ItemMaterialSerializer, it) } ?: CompletableFuture.completedFuture(ItemMaterial.EMPTY)
        return ItemMetadata(material.get(), display.get(), durability.get(), sundry.get())
    }

    /**
     * 序列化 (强制开启结构化解析)
     */
    override fun serialize(component: ItemMetadata) = forceStructured(json.encodeToJsonElement(ItemMetadata.serializer(), component))

    /**
     * 占据的节点
     */
    val occupiedNodes = serializers.flatMap { it.descriptor.elementAlias }

    val NODES_MATERIAL = ItemMetadata.serializer().descriptor.getElementNames(ItemMetadata::material.name)

    /**
     * 结构化解析
     */
    const val NODE_STRUCTURED = "structured"

    internal fun isStructured(element: JsonElement): Boolean = try {
        element.jsonObject[NODE_STRUCTURED]!!.jsonPrimitive.boolean
    } catch (_: Exception) {
        false
    }

    internal fun forceStructured(element: JsonElement): JsonElement = try {
        element.jsonObject.handle { put(NODE_STRUCTURED, JsonPrimitive(true)) }
    } catch (_: Exception) {
        element
    }

}