@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.BukkitSkull
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemSkull TODO SkullSource
 *
 * @author TheFloodDragon
 * @since 2024/10/1 13:48
 */
@Serializable(ItemSkull.Serializer::class)
@KeepGeneratedSerializer
class ItemSkull(
    @JsonNames("skull")
    val head: String? = null,
) {

    @Transient
    private val itemStack: ItemStack? = if (head != null) fetchSkull(head) else null

    private val tag: NbtCompound? by lazy {
        itemStack?.let { RefItemStack.of(it).tag }
    }

    companion object {

        /**
         * 头颅缓存
         */
        private val CACHE: MutableMap<String, ItemStack> = ConcurrentHashMap()

        /**
         * 获取头颅数据
         */
        fun fetchSkull(value: String): ItemStack {
            return CACHE.computeIfAbsent(value.trim()) { generateSkullItem(it) }.clone()
        }

        /**
         * 生成纯头颅数据的 [ItemStack]
         */
        fun generateSkullItem(value: String): ItemStack {
            return BukkitSkull.applySkull(value)
        }

        /**
         * 读取头颅数据
         */
        fun getSkullValue(skullMeta: SkullMeta): String {
            return BukkitSkull.getSkullValue(skullMeta)
        }

        /**
         * 读取头颅数据
         */
        fun getSkullValue(itemStack: ItemStack): String? {
            val meta = itemStack.itemMeta as? SkullMeta ?: return null
            return getSkullValue(meta)
        }

    }

    private object Serializer : KSerializer<ItemSkull> {

        override val descriptor get() = generatedSerializer().descriptor

        override fun serialize(encoder: Encoder, value: ItemSkull) {
            if (encoder is NbtEncoder) {
                encoder.encodeNbtTag(value.tag ?: NbtCompound())
            } else generatedSerializer().serialize(encoder, value)
        }

        override fun deserialize(decoder: Decoder): ItemSkull {
            return if (decoder is NbtDecoder) {
                val ref = RefItemStack.of(XMaterial.PLAYER_HEAD)
                ref.tag = decoder.decodeNbtTag() as? NbtCompound ?: return ItemSkull(null)
                ItemSkull(getSkullValue(ref.bukkitStack))
            } else generatedSerializer().deserialize(decoder)
        }

    }

}