package cn.fd.ratziel.module.itemengine

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.message.builder.ComponentSerializer
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.core.util.replaceNonEscaped
import cn.fd.ratziel.module.itemengine.item.builder.DefaultItemGenerator
import cn.fd.ratziel.module.itemengine.item.builder.ItemMetadataSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.AttributeModifierSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.AttributeSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.EnchantmentSerializer
import cn.fd.ratziel.module.itemengine.item.meta.serializers.HideFlagSerializer
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

        println("<red> \\<nmsl> \\<6\\>  <7\\> <cnm>".replaceNonEscaped("<", "{").replaceNonEscaped(">", "}"))

        println("————————————————————————————————")
        println(meta.nbt.toMapDeep())

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}