package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.module.item.nbt.NMSUtil
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.DynamicOpsNBT
import net.minecraft.nbt.NBTTagCompound
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
            ReflexClass.of(customDataClass).structure.getConstructorByType(NMSUtil.NtCompound.nmsClass)
        }

    }

}

@Suppress("unused")
class NMS12005Impl : NMS12005() {

    override fun parse(nbt: Any): Any? =
        DataComponentPatch.CODEC.parse(DynamicOpsNBT.INSTANCE, nbt as NBTTagCompound)
            .resultOrPartial { error("Failed to parse: $it") }.getOrNull()

    override fun save(dcp: Any): Any? =
        DataComponentPatch.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, dcp as DataComponentPatch)
            .resultOrPartial { error("Failed to save: $it") }.getOrNull()

}