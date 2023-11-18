package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.adventure.ComponentSerializer
import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
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
import cn.fd.ratziel.module.item.util.nbt.toNMS
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
@ElementConfig(sync = true)
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

        println(meta.display)
        println(meta.characteristic)
        println(meta.durability)
        println(meta.nbt.toNMS())

        println("————————————————")

        val testMeta = generator.build(meta)

        println(testMeta)

//        println("——————NMS复合NBT合并测试——————")
//        val nbt = NBTCompound.new()
//        RefItemMeta.applyToItem(testMeta, nbt)
//        println("原始NBT: $nbt")
//        println(meta.nbt.toNMS().javaClass.name)
//        println(nbt.javaClass.name)
//        NBTCompound.merge(nbt, meta.nbt.toNMS()).let { println(it) }
//        println(nbt.invokeMethod("a",nbt))
//        println("合并后的NBT: $nbt")

//        println("——————RefItemMeta方法测试——————")
//        val enchants = meta.characteristic.enchants!!
//        val enchantsTag = NBTCompound.new()
//        RefItemMeta.applyEnchantments(enchantsTag, enchants)
//        println(enchantsTag)


    } catch (ex: Exception) {
        ex.printStackTrace()
    }

}