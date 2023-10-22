package cn.fd.ratziel.item.nms.deprecated

import cn.fd.ratziel.item.nms.deprecated.api.RefClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass

/**
 * RefNBTTagCompound
 *
 *   1.17+ net.minecraft.nbt.NBTTagCompound
 *   1.16- net.minecraft.server.v1_16_R3.NBTTagCompound
 *
 * @author TheFloodDragon
 * @since 2023/10/21 21:40
 */
class RefNBTTagCompound(unresolved: Any?) : RefClass(unresolved) {

    override val clazz: Class<*> by lazy {
        if (MinecraftVersion.isLowerOrEqual(MinecraftVersion.V1_16))
            nmsClass("NBTTagCompound")
        else Class.forName("net.minecraft.nbt.NBTTagCompound")
    }

    override fun get(): Any? = obj

}