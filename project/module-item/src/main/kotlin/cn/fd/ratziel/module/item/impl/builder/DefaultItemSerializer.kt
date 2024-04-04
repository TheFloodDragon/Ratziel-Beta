package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.common.message.builder.MessageComponentSerializer
import cn.fd.ratziel.core.function.futureAsync
import cn.fd.ratziel.core.function.throwable
import cn.fd.ratziel.core.serialization.JsonHandler
import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.module.item.api.common.ItemKSerializer
import cn.fd.ratziel.module.item.impl.part.VItemDisplay
import cn.fd.ratziel.module.item.impl.part.VItemMeta
import cn.fd.ratziel.module.item.impl.part.serializers.AttributeModifierSerializer
import cn.fd.ratziel.module.item.impl.part.serializers.AttributeSerializer
import cn.fd.ratziel.module.item.impl.part.serializers.EnchantmentSerializer
import cn.fd.ratziel.module.item.impl.part.serializers.HideFlagSerializer
import cn.fd.ratziel.module.item.nbt.NBTData
import cn.fd.ratziel.module.item.nbt.NBTSerializer
import cn.fd.ratziel.module.item.util.usedNodes
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag

/**
 * DefaultItemSerializer - 通过 [Json] 序列化/反序列化 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:58
 */
class DefaultItemSerializer(
    rawJson: Json
) : ItemKSerializer<VItemMeta> {

    val json = Json(rawJson) {
        serializersModule += SerializersModule {
            // Common Serializers
            contextual(NBTData::class, NBTSerializer)
            contextual(Component::class, MessageComponentSerializer)
            contextual(EnhancedListSerializer(MessageComponentSerializer))
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
        if (element.isStructured()) return json.decodeFromJsonElement(VItemMeta.serializer(), element)
        // 一般解析
        val display = futureAsync { json.decodeFromJsonElement(VItemDisplay.serializer(), element) }.throwable()
        return VItemMeta(display.get())
    }

    /**
     * 序列化 (强制开启结构化解析)
     */
    override fun serialize(component: VItemMeta) = json.encodeToJsonElement(VItemMeta.serializer(), component).run { forceStructured() }

    override fun getOccupiedNodes() = occupiedNodes

    override val descriptor = VItemMeta.serializer().descriptor

    companion object {

        /**
         * 占据的节点
         */
        val occupiedNodes by lazy {
            VItemMeta.serializer().descriptor.usedNodes.toTypedArray()
        }

        /**
         * 结构化解析
         */
        const val NODE_STRUCTURED = "structured"

        private fun JsonElement.isStructured(): Boolean = try {
            this.jsonObject[NODE_STRUCTURED]!!.jsonPrimitive.boolean
        } catch (_: Exception) {
            false
        }

        private fun JsonElement.forceStructured(): JsonElement = try {
            JsonHandler.edit(this.jsonObject) { put(NODE_STRUCTURED, JsonPrimitive(true)) }
        } catch (_: Exception) {
            this
        }

    }


}