@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.RefItemStack
import cn.fd.ratziel.module.item.internal.serializers.HideFlagSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames
import taboolib.library.xseries.XItemFlag
import taboolib.library.xseries.XMaterial

typealias HideFlag = @Serializable(HideFlagSerializer::class) XItemFlag

/**
 * ItemHideFlag
 *
 * @author TheFloodDragon
 * @since 2025/4/30 21:55
 */
@Serializable(ItemHideFlag.Serializer::class)
@KeepGeneratedSerializer
class ItemHideFlag(
    /**
     * 物品隐藏标签
     */
    @JsonNames("hideflag", "hideflags", "hideFlag")
    var hideFlags: MutableSet<HideFlag>? = null,
) {

    /**
     * 添加物品隐藏标签
     */
    fun add(vararg flags: HideFlag) {
        val hideFlags = this.hideFlags
        if (hideFlags == null) {
            this.hideFlags = flags.toMutableSet()
        } else hideFlags.addAll(flags)
    }

    /**
     * 删除物品隐藏标签
     */
    fun remove(vararg flags: HideFlag) {
        hideFlags?.removeAll(flags.toSet())
    }

    private object Serializer : KSerializer<ItemHideFlag> {

        override val descriptor get() = generatedSerializer().descriptor

        override fun serialize(encoder: Encoder, value: ItemHideFlag) {
            if (encoder is NbtEncoder) {
                val ref = RefItemStack.of(XMaterial.STONE)
                val flags = value.hideFlags?.mapNotNull { it.get() }
                if (flags != null) {
                    ref.bukkitStack.apply {
                        itemMeta = itemMeta.also {
                            it?.addItemFlags(*flags.toTypedArray())
                        }
                    }
                }
                encoder.encodeNbtTag(ref.tag)
            } else generatedSerializer().serialize(encoder, value)
        }

        override fun deserialize(decoder: Decoder): ItemHideFlag {
            return if (decoder is NbtDecoder) {
                val ref = RefItemStack.of(XMaterial.STONE)
                ref.tag = decoder.decodeNbtTag() as? NbtCompound ?: return ItemHideFlag(null)
                val flags = XItemFlag.getFlags(ref.bukkitStack).toMutableSet()
                ItemHideFlag(flags)
            } else generatedSerializer().deserialize(decoder)
        }

    }

}