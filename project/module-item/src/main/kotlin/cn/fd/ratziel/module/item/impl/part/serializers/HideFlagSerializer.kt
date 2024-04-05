package cn.fd.ratziel.module.item.impl.part.serializers

import cn.fd.ratziel.module.item.util.MetaMather
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.ItemFlag
import java.util.*

/**
 * HideFlagSerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/3 19:54
 */
object HideFlagSerializer : KSerializer<ItemFlag> {

    override val descriptor = PrimitiveSerialDescriptor("bukkit.ItemFlag", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ItemFlag = MetaMather.matchItemFlag(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: ItemFlag) = encoder.encodeString(value.name)

    /**
     * 物品隐藏标签 Bit 与 ItemFlag 之间的转换
     */
    fun translateFlags(hideFlags: Iterable<ItemFlag>): Int {
        var bitFlag = 0
        hideFlags.forEach {
            bitFlag = bitFlag or getBitModifier(it).toInt()
        }
        return bitFlag
    }

    fun getFlagFromBit(bitFlag: Int): Set<ItemFlag> =
        EnumSet.noneOf(ItemFlag::class.java).also { currentFlags ->
            ItemFlag.entries.forEach {
                if (hasFlag(bitFlag, it)) {
                    currentFlags.add(it)
                }
            }
        }

    fun hasFlag(bitFlag: Int, flag: ItemFlag) =
        getBitModifier(flag).toInt().let { bitModifier ->
            (bitFlag and bitModifier) == bitModifier
        }

    fun getBitModifier(hideFlag: ItemFlag) = (1 shl hideFlag.ordinal).toByte()

}