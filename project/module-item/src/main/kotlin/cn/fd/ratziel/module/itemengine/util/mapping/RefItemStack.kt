package cn.fd.ratziel.module.itemengine.util.mapping

import cn.fd.ratziel.core.function.getMethodUnsafe
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass

/**
 * RefItemStack
 *
 * @author TheFloodDragon
 * @since 2023/10/28 16:18
 */
object RefItemStack {

    /**
     * (obc)ItemStack
     */
    @JvmStatic
    val obcClass by lazy {
        obcClass("inventory.CraftItemStack")
    }

    /**
     * (nms)ItemStack
     *   1.17+ net.minecraft.world.item.ItemStack
     *   1.17- net.minecraft.server.$VERSION.ItemStack
     */
    @JvmStatic
    val nmsClass by lazy {
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17))
            Class.forName("net.minecraft.world.item.ItemStack")
        else nmsClass("ItemStack")
    }

    /**
     * CraftItemStack#handle{net.minecraft.world.item.ItemStack}
     * 注意: 此方法忽略了空值问题
     */
    @JvmStatic
    fun nmsFromObc(obc: Any) = obc.getProperty<Any?>("handle")!!

    /**
     * 获取 Nms 下的 ItemStack 中的 NBTTagCompound
     */
    @JvmStatic
    fun tagFromNms(nms: Any) = NBTCompound.of(nmsTagGetMethod.invoke(nms)!!)

    /**
     * 获取 Nms 下的 ItemStack 中的 NBTTagCompound
     */
    @JvmStatic
    fun tagFromObc(obc: Any) = tagFromNms(nmsFromObc(obc))

    internal val nmsTagGetMethod by lazy {
        ReflexClass.of(nmsClass).structure.getMethodUnsafe(
            name = if (MinecraftVersion.isUniversal) "w" else "getTag",
            returnType = NBTCompound.clazz::class.java
        )
    }

}