package cn.fd.ratziel.module.item.util

import cn.altawk.nbt.NbtDecoder
import cn.altawk.nbt.NbtEncoder
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.BukkitItemStack
import cn.fd.ratziel.module.item.internal.nms.RefItemMeta
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.BukkitSkull
import java.util.concurrent.ConcurrentHashMap

/**
 * SkullUtil - 头颅工具
 *
 * @author TheFloodDragon
 * @since 2024/8/12 16:13
 */
object SkullUtil {

    /**
     * 头颅缓存
     */
    private val CACHE: MutableMap<String, SkullData> = ConcurrentHashMap()

    /**
     * 获取头颅数据
     */
    fun fetchSkullData(value: String): SkullData {
        return CACHE.computeIfAbsent(value) { SkullData(generateSkullItem(value)) }
    }

    /**
     * 获取头颅数据
     */
    fun fetchSkullData(skullMeta: SkullMeta): SkullData {
        return fetchSkullData(getSkullValue(skullMeta))
    }

    /**
     * 生成纯头颅数据的 [BukkitItemStack]
     */
    fun generateSkullItem(value: String): BukkitItemStack {
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
    fun getSkullData(tag: NbtCompound): SkullData {
        val item = RefItemStack.of(XMaterial.PLAYER_HEAD.parseItem()!!)
        item.tag = tag
        return SkullData(item.bukkitStack)
    }

}

@Serializable(SkullData.Companion::class)
class SkullData(val item: BukkitItemStack) {

    val meta: SkullMeta by lazy {
        item.itemMeta!! as SkullMeta
    }

    val tag: NbtCompound by lazy {
        RefItemMeta.of(RefItemMeta.META_SKULL).applyToTag(NbtCompound())
    }

    companion object : KSerializer<SkullData> {

        override val descriptor = PrimitiveSerialDescriptor("item.SkullData", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): SkullData {
            if (decoder is NbtDecoder) {
                val tag = decoder.decodeNbtTag()
                if (tag !is NbtCompound) throw IllegalStateException("Invalid NBT tag: '$tag'")
                return SkullUtil.getSkullData(tag)
            } else return SkullUtil.fetchSkullData(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: SkullData) {
            if (encoder is NbtEncoder) {
                encoder.encodeNbtTag(value.tag)
            } else encoder.encodeString(SkullUtil.getSkullValue(value.meta))
        }

    }

}
