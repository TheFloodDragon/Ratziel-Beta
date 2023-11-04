package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.adventure.ComponentSerializer
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.log
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.module.item.impl.DefaultItemGenerator
import cn.fd.ratziel.module.item.impl.ItemMetadataSerializer
import cn.fd.ratziel.module.item.item.meta.serializers.AttributeModifierSerializer
import cn.fd.ratziel.module.item.item.meta.serializers.AttributeSerializer
import cn.fd.ratziel.module.item.item.meta.serializers.EnchantmentSerializer
import cn.fd.ratziel.module.item.item.meta.serializers.ItemFlagSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.text.Component
import taboolib.library.reflex.Reflex.Companion.getProperty

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
            contextual(org.bukkit.inventory.ItemFlag::class, ItemFlagSerializer)
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
        println(element.property)

        val serializer = ItemMetadataSerializer()
        val generator = DefaultItemGenerator()

        val meta = serializer.serializeByJson(json, element.property)

        log(meta.display)
        log(meta.characteristic)
        log(meta.durability)
        log(meta.nbt)

        log("————————————————")

        val testMeta = generator.build(meta)

        log(testMeta)
        log(testMeta.getProperty("displayName"))

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}