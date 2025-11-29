package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.internal.nms.NMSItem.Companion.isModern
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTBase
import net.minecraft.resources.RegistryOps
import org.bukkit.craftbukkit.v1_21_R4.CraftRegistry

/**
 * CodecSerialization
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 21:36
 */
object CodecSerialization {

    @JvmStatic
    val nmsOps: RegistryOps<NBTBase> by lazy {
        CraftRegistry.getMinecraftRegistry().createSerializationContext(DynamicOpsNBT.INSTANCE)
    }

    @JvmStatic
    val modernOps: RegistryOps<NbtTag> by lazy {
        CraftRegistry.getMinecraftRegistry().createSerializationContext(ModernNbtOps)
    }

    @JvmStatic
    fun <T> parseFromTag(codec: Codec<T>, tag: NbtTag): T {
        val result: DataResult<T> = if (isModern) {
            codec.parse(modernOps, tag)
        } else {
            val nmsTag = NMSNbt.INSTANCE.toNms(tag) as NBTBase
            codec.parse(nmsOps, nmsTag)
        }
        return result.getOrThrow { error("Failed to parse: $it") }
    }

    @JvmStatic
    fun <T> saveToTag(codec: Codec<T>, value: T): NbtTag {
        val result: DataResult<NbtTag> = if (isModern) {
            codec.encodeStart(modernOps, value)
        } else {
            codec.encodeStart(nmsOps, value).map {
                NMSNbt.INSTANCE.fromNms(it)
            }
        }
        return result.getOrThrow { error("Failed to save: $it") }
    }

}