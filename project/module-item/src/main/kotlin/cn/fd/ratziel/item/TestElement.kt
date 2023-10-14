package cn.fd.ratziel.item

import cn.fd.ratziel.adventure.ComponentSerializer
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.core.serialization.handlePrimitives
import cn.fd.ratziel.core.serialization.serializers.EnhancedListSerializer
import cn.fd.ratziel.item.meta.OItemMeta
import cn.fd.ratziel.item.meta.serializers.AttributeModifierSerializer
import cn.fd.ratziel.item.meta.serializers.AttributeSerializer
import cn.fd.ratziel.item.meta.serializers.EnchantmentSerializer
import cn.fd.ratziel.item.meta.serializers.ItemFlagSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag

/**
 * TestElement
 *
 * @author TheFloodDragon
 * @since 2023/10/3 13:16
 */
@NewElement(
    "meta",
    space = "test"
)
class TestElement : ElementHandler {

    val serializers by lazy {
        SerializersModule {
            contextual(Component::class, ComponentSerializer)
            contextual(EnhancedListSerializer(ComponentSerializer))
            contextual(Enchantment::class, EnchantmentSerializer)
            contextual(ItemFlag::class, ItemFlagSerializer)
            contextual(Attribute::class, AttributeSerializer)
            contextual(AttributeModifier::class, AttributeModifierSerializer)
        }
    }

    val json by lazy {
        Json(baseJson) {
            serializersModule = serializers
        }
    }

    override fun handle(element: Element) = try {
        println(element.property)

        handlePrimitives(element.property) { if (it.isString) JsonPrimitive(it.content.replace("1", "2")) else it }

        val meta = json.decodeFromJsonElement<OItemMeta>(element.property)

        println(meta)

    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}