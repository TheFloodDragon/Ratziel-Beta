@file:Suppress("unused")

package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.module.item.nbt.NMSUtil
import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R4.CraftServer
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.nmsProxy
import kotlin.jvm.optionals.getOrNull

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
    abstract fun parse(nbt: Any): Any?

    /**
     * [DataComponentPatch] to [NBTTagCompound]
     */
    abstract fun save(dcp: Any): Any?

    companion object {

        val INSTANCE by lazy {
            // Version Check
            if (MinecraftVersion.majorLegacy < 12005) throw UnsupportedOperationException("NMS12005 is only available after Minecraft 1.20.5!")
            // NmsProxy
            nmsProxy<NMS12005>()
        }

        /**
         * [net.minecraft.world.item.component.CustomData]
         */
        val customDataClass by lazy {
            nmsClass("CustomData")
        }

        /**
         * private CustomData(NBTTagCompound var0)
         */
        val customDataConstructor by lazy {
            ReflexClass.of(customDataClass).getConstructor(NMSUtil.NtCompound.nmsClass)
        }

    }

}

class NMS12005Impl : NMS12005() {

//    val access: IRegistryCustom.Dimension = (Bukkit.getServer() as CraftServer).server.registryAccess()
//
//    override fun parse(nbt: Any): Any? =
//        DataComponentPatch.CODEC.parse(access.createSerializationContext(DynamicOpsNBT.INSTANCE), nbt as NBTTagCompound)
//            .resultOrPartial { error("Failed to parse NBT: $nbt") }.getOrNull()
//
//    override fun save(dcp: Any): Any? =
//        DataComponentPatch.CODEC.encodeStart(access.createSerializationContext(DynamicOpsNBT.INSTANCE), dcp as DataComponentPatch)
//            .resultOrPartial { error("Failed to save DataComponentPatch: $dcp") }.getOrNull()
//

    val access = (Bukkit.getServer() as CraftServer).server.bc()

    override fun parse(nbt: Any): Any? =
        DataComponentPatch::class.java.getProperty<Codec<DataComponentPatch>>("b", isStatic = true)!!
            .parse(access.a(DynamicOpsNBT.a), nbt as NBTTagCompound)
            .resultOrPartial { error("Failed to parse NBT: $nbt") }.getOrNull()

    override fun save(dcp: Any): Any? =
        DataComponentPatch::class.java.getProperty<Codec<DataComponentPatch>>("b", isStatic = true)!!
            .encodeStart(access.a(DynamicOpsNBT.a), dcp as DataComponentPatch)
            .resultOrPartial { error("Failed to save DataComponentPatch: $dcp") }.getOrNull()

//    override fun new(): Any = PatchedDataComponentMap(DataComponentMap.EMPTY)
//
//    override fun get(pdc: Any, type: Any): Any? = (pdc as PatchedDataComponentMap).get(type as DataComponentType<*>)
//
//    override fun set(pdc: Any, type: Any, value: Any): Any? = (pdc as PatchedDataComponentMap).set(uncheck<DataComponentType<in Any>>(type), value)
//
//    override fun clone(pdc: Any): Any = (pdc as PatchedDataComponentMap).copy()
//
//    override fun search(name: String): Any? {
//        for (entry in typeMap) {
//            if (entry.key.path.equals(name)) return entry.value
//        }
//        return null
//    }
//
//    val typeMap: Map<MinecraftKey, DataComponentType<*>> by lazy {
//        buildMap {
//            val registry = BuiltInRegistries.DATA_COMPONENT_TYPE
//            registry.forEach {
//                put(registry.getKey(it)!!, it)
//            }
//        }
//    }

}