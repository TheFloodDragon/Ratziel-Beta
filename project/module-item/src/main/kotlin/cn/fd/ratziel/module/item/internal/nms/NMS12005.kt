package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NBTBase
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NBTHelper
import cn.altawk.nbt.tag.NBTTagCompound
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.resources.RegistryOps
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R4.CraftServer
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy


/**
 * NMS12005
 *
 * 用于 1.20.5+ 的 [DataComponentPatch] 相关使用
 *
 * @author TheFloodDragon
 * @since 2024/5/5 12:39
 */
abstract class NMS12005 {

    /**
     * [NbtCompound] to [DataComponentPatch]
     */
    abstract fun parsePatch(tag: NbtCompound): Any?

    /**
     * [DataComponentPatch] to [NbtCompound]
     */
    abstract fun savePatch(dcp: Any): NbtCompound?

    /**
     * [NbtCompound] to [DataComponentMap]
     */
    abstract fun parseMap(tag: NbtCompound): Any?

    /**
     * [DataComponentMap] to [NbtCompound]
     */
    abstract fun saveMap(dcm: Any): NbtCompound?

    companion object {

        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005) nmsProxy<NMS12005>() else throw UnsupportedOperationException("NMS12005 is only available after Minecraft 1.20.5!")
        }

        /**
         * [net.minecraft.core.component.DataComponentPatch]
         */
        val DATA_COMPONENT_PATCH_CLASS by lazy {
            nmsClass("DataComponentPatch")
        }

    }

}

@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMS12005Impl : NMS12005() {

    fun <T> parse0(codec: Codec<T>, obj: NBTTagCompound): T? {
        val result = codec.parse(access.createSerializationContext(DynamicOpsNBT.INSTANCE), obj)
        val opt = result.resultOrPartial { error("Failed to parse: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> save0(codec: Codec<T>, obj: T): NBTBase? {
        val result = codec.encodeStart(access.createSerializationContext(DynamicOpsNBT.INSTANCE), obj)
        val opt = result.resultOrPartial { error("Failed to save: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> parseFromTag(codec: Codec<T>, tag: NbtCompound): T? {
        return parse0(codec, NBTHelper.toNms(tag) as NBTTagCompound)
    }

    fun <T> saveToTag(codec: Codec<T>, obj: T): NbtCompound? {
        return save0(codec, obj)?.let { NBTHelper.fromNms(it as NBTTagCompound) as NbtCompound }
    }

    override fun parsePatch(tag: NbtCompound): Any? = parseFromTag(DataComponentPatch.CODEC, tag)

    override fun savePatch(dcp: Any): NbtCompound? = saveToTag(DataComponentPatch.CODEC, dcp as DataComponentPatch)

    override fun parseMap(tag: NbtCompound): Any? = parseFromTag(DataComponentMap.CODEC, tag)

    override fun saveMap(dcm: Any): NbtCompound? = saveToTag(DataComponentMap.CODEC, dcm as DataComponentMap)

    private val access: net.minecraft.core.HolderLookup.a by lazy {
        (Bukkit.getServer() as CraftServer).server.registryAccess()
    }

    private val method by lazy {
        val ref = ReflexClass.of(net.minecraft.core.HolderLookup.a::class.java, false)
        ref.structure.getMethodByTypeSilently("a", DynamicOps::class.java)
            ?: ref.structure.getMethodByType("createSerializationContext", DynamicOps::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> createSerializationContext(dynamicOps: DynamicOps<V>): RegistryOps<V> = method.invoke(access, dynamicOps)!! as RegistryOps<V>

}