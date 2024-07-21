package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.function.util.uncheck
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import net.minecraft.core.IRegistryCustom
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTTagCompound
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
     * [NBTTagCompound] to [DataComponentPatch]
     */
    abstract fun parsePatch(nbt: Any): Any?

    /**
     * [DataComponentPatch] to [NBTTagCompound]
     */
    abstract fun savePatch(dcp: Any): Any?

    /**
     * [NBTTagCompound] to [DataComponentMap]
     */
    abstract fun parseMap(nbt: Any): Any?

    /**
     * [DataComponentMap] to [NBTTagCompound]
     */
    abstract fun saveMap(dcm: Any): Any?

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

@Suppress("unused")
class NMS12005Impl : NMS12005() {

    fun <T> parse(codec: Codec<T>, nbt: NBTTagCompound): T? {
        val result = codec.parse(createSerializationContext(DynamicOpsNBT.INSTANCE), nbt)
        val opt = result.resultOrPartial { error("Failed to parse: $it") }
        return if (opt.isPresent) opt.get() else null
    }

    fun <T> save(codec: Codec<T>, obj: T): NBTTagCompound? {
        val result = codec.encodeStart(createSerializationContext(DynamicOpsNBT.INSTANCE), obj)
        val opt = result.resultOrPartial { error("Failed to save: $it") }
        return if (opt.isPresent) opt.get() as? NBTTagCompound else null
    }

    override fun parsePatch(nbt: Any): Any? = parse(DataComponentPatch.CODEC, nbt as NBTTagCompound)

    override fun savePatch(dcp: Any): Any? = save(DataComponentPatch.CODEC, dcp as DataComponentPatch)

    override fun parseMap(nbt: Any): Any? = parse(DataComponentMap.CODEC, nbt as NBTTagCompound)

    override fun saveMap(dcm: Any): Any? = save(DataComponentMap.CODEC, dcm as DataComponentMap)

    val access: IRegistryCustom.Dimension by lazy { (Bukkit.getServer() as CraftServer).server.registryAccess() }

    val method by lazy {
        val ref = ReflexClass.of(net.minecraft.core.HolderLookup.a::class.java, false)
        ref.structure.getMethodByTypeSilently("a", DynamicOps::class.java)
            ?: ref.structure.getMethodByType("createSerializationContext", DynamicOps::class.java)
    }

    fun <V> createSerializationContext(dynamicOps: DynamicOps<V>): RegistryOps<V> = uncheck(method.invoke(access, dynamicOps)!!)

}