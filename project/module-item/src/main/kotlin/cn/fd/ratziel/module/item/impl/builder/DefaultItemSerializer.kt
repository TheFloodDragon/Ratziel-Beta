package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.get
import cn.fd.ratziel.core.serialization.getElementDescriptor
import cn.fd.ratziel.core.serialization.handle
import cn.fd.ratziel.core.serialization.usedNodes
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.common.ItemKSerializer
import cn.fd.ratziel.module.item.impl.part.VItemDisplay
import cn.fd.ratziel.module.item.impl.part.VItemDurability
import cn.fd.ratziel.module.item.impl.part.VItemMeta
import cn.fd.ratziel.module.item.impl.part.VItemSundry
import cn.fd.ratziel.module.item.impl.part.serializers.*
import cn.fd.ratziel.module.item.nbt.NBTData
import cn.fd.ratziel.module.item.nbt.NBTSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemSerializer - 通过 [Json] 序列化/反序列化 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:58
 */
class DefaultItemSerializer(rawJson: Json) : ItemKSerializer<VItemMeta> {

    val json = Json(rawJson) {
        serializersModule += SerializersModule {
            // Common Serializers
            contextual(NBTData::class, NBTSerializer)
            contextual(ItemMaterial::class, ItemMaterialSerializer)
            // Bukkit Serializers
            contextual(Enchantment::class, EnchantmentSerializer)
            contextual(ItemFlag::class, HideFlagSerializer)
            contextual(Attribute::class, AttributeSerializer)
            contextual(AttributeModifier::class, AttributeModifierSerializer)
        }
    }

    /**
     * 反序列化 (检查结构化解析)
     */
    override fun deserialize(element: JsonElement): VItemMeta {
        // 结构化解析
        if (isStructured(element)) return json.decodeFromJsonElement(VItemMeta.serializer(), element)
        // 异步方法
        fun <T> asyncDecode(deserializer: DeserializationStrategy<T>, from: JsonElement = element) =
            CompletableFuture.supplyAsync {
                json.decodeFromJsonElement(deserializer, from)
            }.exceptionally { it.printStackTrace();null }
        // 一般解析
        val display = asyncDecode(VItemDisplay.serializer())
        val durability = asyncDecode(VItemDurability.serializer())
        val sundry = asyncDecode(VItemSundry.serializer())
        val material = (element as? JsonObject)?.get(NODES_MATERIAL)
            ?.let { asyncDecode(ItemMaterialSerializer, it) } ?: CompletableFuture.completedFuture(ItemMaterial.EMPTY)
        return VItemMeta(material.get(), display.get(), durability.get(), sundry.get())
    }

    /**
     * 序列化 (强制开启结构化解析)
     */
    override fun serialize(component: VItemMeta) = forceStructured(json.encodeToJsonElement(VItemMeta.serializer(), component))

    override val descriptor = VItemMeta.serializer().descriptor

    companion object {

        val serializers = arrayOf(
            VItemMeta.serializer(),
            VItemDisplay.serializer(),
            VItemDurability.serializer(),
            VItemSundry.serializer(),
        )

        val NODES_MATERIAL = VItemMeta.serializer().descriptor.getElementDescriptor(VItemMeta::material.name).usedNodes

        /**
         * 占据的节点
         */
        val occupiedNodes = serializers.flatMap { it.descriptor.usedNodes }

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


}