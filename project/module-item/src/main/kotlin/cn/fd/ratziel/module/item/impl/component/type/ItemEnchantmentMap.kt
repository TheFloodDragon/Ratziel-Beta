package cn.fd.ratziel.module.item.impl.component.type

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.fd.ratziel.module.item.internal.serializers.EnchantmentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.enchantments.Enchantment
import taboolib.library.xseries.XEnchantment
import java.util.LinkedHashMap

typealias EnchantmentType = @Serializable(EnchantmentSerializer::class) XEnchantment

/**
 * ItemEnchantmentMap
 *
 * 附魔等级表，直接以可变 Map 形式暴露给组件系统使用。
 *
 * @author TheFloodDragon
 * @since 2026/4/5 18:46
 */
@Serializable(ItemEnchantmentMap.Serializer::class)
class ItemEnchantmentMap(
    handle: MutableMap<EnchantmentType, Int> = HashMap(),
) : MutableMap<EnchantmentType, Int> by handle {

    constructor(vararg entries: Pair<EnchantmentType, Int>) : this(
        HashMap<EnchantmentType, Int>(entries.size).apply { putAll(entries) }
    )

    /**
     * 转换为 Bukkit 原生附魔表。
     */
    fun toBukkitMap(): Map<Enchantment, Int> {
        return this.mapKeys { (enchantment, _) -> enchantment.get()!! }
    }

    object Serializer : KSerializer<ItemEnchantmentMap> {

        private val delegate = MapSerializer(EnchantmentSerializer, Int.serializer())

        override val descriptor: SerialDescriptor get() = delegate.descriptor

        override fun serialize(encoder: Encoder, value: ItemEnchantmentMap) {
            if (encoder is NbtEncoder) {
                throw UnsupportedOperationException(
                    "ItemEnchantmentMap does not support direct NBT serialization; use EnchantmentsNbtTransformer instead.",
                )
            }
            delegate.serialize(encoder, value)
        }

        override fun deserialize(decoder: Decoder): ItemEnchantmentMap {
            if (decoder is NbtDecoder) {
                throw UnsupportedOperationException(
                    "ItemEnchantmentMap does not support direct NBT deserialization; use EnchantmentsNbtTransformer instead.",
                )
            }
            return ItemEnchantmentMap(delegate.deserialize(decoder).toMutableMap())
        }

    }

    companion object {

        /**
         * 将 Bukkit 原生附魔表转换为组件使用的附魔表。
         */
        @JvmStatic
        fun fromBukkitMap(source: Map<Enchantment, Int>): ItemEnchantmentMap {
            val mapped = HashMap<EnchantmentType, Int>(source.size)
            source.forEach { (enchantment, level) ->
                mapped[XEnchantment.of(enchantment)] = level
            }
            return ItemEnchantmentMap(mapped)
        }

    }

}
