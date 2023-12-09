package cn.fd.ratziel.module.itemengine

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.message.builder.ComponentSerializer
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemGenerator
import cn.fd.ratziel.module.itemengine.item.builder.ItemMetadataSerializer
import cn.fd.ratziel.module.itemengine.item.meta.VItemCharacteristic
import cn.fd.ratziel.module.itemengine.item.meta.VItemDisplay
import cn.fd.ratziel.module.itemengine.item.meta.serializers.AttributeModifierSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.AttributeSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.EnchantmentSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.HideFlagSerializer
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
import cn.fd.ratziel.module.itemengine.util.mapping.RefItemMeta
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.text.Component

/**
 * ItemElement
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:55
 */
@NewElement(
    "meta",
    space = "test"
)
object ItemElement : ElementHandler {

    val serializers by lazy {
        SerializersModule {
            // Common Serializers
            contextual(Component::class, ComponentSerializer)
            contextual(EnhancedListSerializer(ComponentSerializer))
            // Bukkit Serializers
            contextual(org.bukkit.enchantments.Enchantment::class, EnchantmentSerializer)
            contextual(org.bukkit.inventory.ItemFlag::class, HideFlagSerializer)
            contextual(org.bukkit.attribute.Attribute::class, AttributeSerializer)
            contextual(org.bukkit.attribute.AttributeModifier::class, AttributeModifierSerializer)
        }
    }

    val json by lazy {
        Json(baseJson) {
            serializersModule = serializers
        }
    }

    override fun handle(element: Element) = try {

        val serializer = ItemMetadataSerializer()
        val generator = DefaultItemGenerator()

        val meta = serializer.serializeByJson(json, element.property)

        println(meta.display)
        println(meta.characteristic)
        println(meta.durability)
        println(meta.nbt)

        println("————————————————————————————————")

        val testMeta = generator.build(meta)

        println(testMeta)

        println(NBTCompound.new().also { RefItemMeta.applyToItem(testMeta, it) })

        println("————————————————————————————————")
        println(VItemDisplay().detransform(meta.display.transform()))
        println(VItemCharacteristic().detransform(meta.characteristic.transform()))

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}