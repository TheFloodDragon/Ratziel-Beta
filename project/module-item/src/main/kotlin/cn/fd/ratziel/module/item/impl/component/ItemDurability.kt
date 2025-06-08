@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames
import taboolib.module.nms.MinecraftVersion

/**
 * ItemDurability - 物品耐久
 *
 * @author TheFloodDragon
 * @since 2024/4/13 16:05
 */
@Serializable
class ItemDurability(
    /**
     * 物品最大耐久 (1.20.5+)
     */
    @JsonNames("maxDamage", "max-damage", "max-durability", "durability")
    var maxDurability: Int? = null,
    /**
     * 物品修复消耗
     */
    @JsonNames("repair-cost")
    var repairCost: Int? = null,
    /**
     * 物品是否无法破坏
     */
    @JsonNames("isUnbreakable")
    var unbreakable: @Serializable(UnbreakableSerializer::class) Boolean? = null,
) {

    private class UnbreakableSerializer : KSerializer<Boolean> {
        override val descriptor = PrimitiveSerialDescriptor("item.durability.unbreakable", PrimitiveKind.BOOLEAN)

        override fun serialize(encoder: Encoder, value: Boolean) {
            // 1.20.5 之后 unbreakable 类型被改成了 Compound
            if (MinecraftVersion.versionId >= 12005 && value) {
                encoder.encodeSerializableValue(Unit.serializer(), Unit)
            } else encoder.encodeBoolean(value)
        }

        override fun deserialize(decoder: Decoder): Boolean {
            // 1.20.5 之后 unbreakable 类型被改成了 Compound
            return if (MinecraftVersion.versionId >= 12005) {
                decoder.decodeNullableSerializableValue(Unit.serializer()) != null
            } else decoder.decodeBoolean()
        }

    }

}