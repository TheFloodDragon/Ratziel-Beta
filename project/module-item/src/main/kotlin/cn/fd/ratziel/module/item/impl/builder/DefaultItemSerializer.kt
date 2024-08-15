package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.serializers.UUIDSerializer
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.impl.component.HideFlag
import cn.fd.ratziel.module.item.impl.component.serializers.*
import cn.fd.ratziel.module.nbt.NBTData
import cn.fd.ratziel.module.nbt.NBTSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import java.util.*

/**
 * DefaultItemSerializer
 *
 * @author TheFloodDragon
 * @since 2024/8/15 12:30
 */
object DefaultItemSerializer {

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
     * 通过 [KSerializer] 创建一个 [ItemSerializer]
     */
    fun <T> createItemSerializer(serializer: KSerializer<T>): ItemSerializer<T> = DelegateItemSerializer(json, serializer)

    /**
     * 托管 [KSerializer] 以实现 [ItemSerializer]
     */
    private class DelegateItemSerializer<T>(val json: Json, val serializer: KSerializer<T>) : ItemSerializer<T>, KSerializer<T> by serializer {
        override fun serialize(component: T) = json.encodeToJsonElement(serializer, component)
        override fun deserialize(element: JsonElement) = json.decodeFromJsonElement(serializer, element)
    }

}