package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.nbt.NBTAdapter
import cn.fd.ratziel.module.nbt.NBTCompound
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JavaOps
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
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
     * [NBTCompound] to [DataComponentPatch]
     */
    abstract fun parsePatch(tag: NBTCompound): Any?

    /**
     * [DataComponentPatch] to [NBTCompound]
     */
    abstract fun savePatch(dcp: Any): NBTCompound?

    /**
     * [NBTCompound] to [DataComponentMap]
     */
    abstract fun parseMap(tag: NBTCompound): Any?

    /**
     * [DataComponentMap] to [NBTCompound]
     */
    abstract fun saveMap(dcm: Any): NBTCompound?

    companion object {

        val INSTANCE by lazy {
            // Version Check
            if (MinecraftVersion.majorLegacy < 12005) throw UnsupportedOperationException("NMS12005 is only available after Minecraft 1.20.5!")
            // NmsProxy
            nmsProxy<NMS12005>()
        }

        /**
         * [net.minecraft.core.component.DataComponentPatch]
         */
        val dataComponentPatchClass by lazy {
            nmsClass("DataComponentPatch")
        }

    }

}

@Suppress("unused", "MemberVisibilityCanBePrivate")
class NMS12005Impl : NMS12005() {

    fun <T> parse0(codec: Codec<T>, tag: Any): T? {
        val result = codec.parse(access.createSerializationContext(JavaOps.INSTANCE), tag)
        val opt = result.resultOrPartial { error("Failed to parse: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> save0(codec: Codec<T>, obj: T): Any? {
        val result = codec.encodeStart(access.createSerializationContext(JavaOps.INSTANCE), obj)
        val opt = result.resultOrPartial { error("Failed to save: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> parse(codec: Codec<T>, tag: NBTCompound): T? {
        return parse0(codec, NBTAdapter.unboxMap(tag))
    }

    fun <T> save(codec: Codec<in T>, obj: T): NBTCompound? {
        return (save0(codec, obj) as? Map<*, *>)?.let { NBTAdapter.boxMap(it) }
    }

    override fun parsePatch(tag: NBTCompound): Any? = parse(DataComponentPatch.CODEC, tag)

    override fun savePatch(dcp: Any): NBTCompound? = save(DataComponentPatch.CODEC, dcp as DataComponentPatch)

    override fun parseMap(tag: NBTCompound): Any? = parse(DataComponentMap.CODEC, tag)

    override fun saveMap(dcm: Any): NBTCompound? = save(DataComponentMap.CODEC, dcm as DataComponentMap)

    fun <V> createSerializationContext(dynamicOps: DynamicOps<V>): RegistryOps<V> = uncheck(method.invoke(access, dynamicOps)!!)

    private val access: net.minecraft.core.HolderLookup.a by lazy {
        (Bukkit.getServer() as CraftServer).server.registryAccess()
    }

    private val method by lazy {
        val ref = ReflexClass.of(net.minecraft.core.HolderLookup.a::class.java, false)
        ref.structure.getMethodByTypeSilently("a", DynamicOps::class.java)
            ?: ref.structure.getMethodByType("createSerializationContext", DynamicOps::class.java)
    }

}