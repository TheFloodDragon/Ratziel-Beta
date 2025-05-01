@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames
import taboolib.library.xseries.XItemFlag
import taboolib.library.xseries.XMaterial

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
    var hideFlags: MutableSet<XItemFlag>? = null,
) {

    /**
     * 添加物品隐藏标签
     */
    fun addHideFlags(vararg flags: XItemFlag) {
        // 初始化 HideFlags
        if (hideFlags == null) {
            hideFlags = HashSet()
        }
        hideFlags?.addAll(flags)
    }

    /**
     * 删除物品隐藏标签
     */
    fun removeHideFlags(vararg flags: XItemFlag) {
        hideFlags?.removeAll(flags)
    }

    private object Serializer : KSerializer<ItemHideFlag> {

        override val descriptor get() = generatedSerializer().descriptor

        override fun serialize(encoder: Encoder, value: ItemHideFlag) {
            if (encoder is NbtEncoder) {
                val ref = RefItemStack.of(XMaterial.STONE)
                val hideFlags = value.hideFlags
                if (hideFlags != null) {
                    for (hideFlag in hideFlags) {
                        hideFlag.set(ref.bukkitStack)
                    }
                }
                encoder.encodeNbtTag(ref.tag)
            } else generatedSerializer().serialize(encoder, value)
        }

        override fun deserialize(decoder: Decoder): ItemHideFlag {
            return if (decoder is NbtDecoder) {
                val ref = RefItemStack.of(XMaterial.STONE)
                ref.tag = decoder.decodeNbtTag() as? NbtCompound ?: return ItemHideFlag(null)
                val flags = XItemFlag.HIDE_DYE.getFlags(ref.bukkitStack)?.toMutableSet()
                ItemHideFlag(flags)
            } else generatedSerializer().deserialize(decoder)
        }

    }

}