package cn.fd.ratziel.module.item.util.ref

import cn.fd.ratziel.module.item.util.nbt.NBTCompound
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.obcClass

/**
 * RefItemMeta - CraftMetaItem映射类
 *
 * @author TheFloodDragon
 * @since 2023/10/22 10:14
 */
object RefItemMeta {

    @JvmStatic
    val metaClass by lazy {
        obcClass("inventory.CraftMetaItem")
    }

    /**
     * CraftMetaItem#constructor(net.minecraft.nbt.NBTTagCompound)
     * @return CraftMetaItem
     */
    @JvmStatic
    fun new(value: Any) = metaClass.invokeConstructor(value)

    /**
     * 创建空对象
     */
    @JvmStatic
    fun new() = new(NBTCompound.new())

    /**
     * CraftMetaItem#applyToItem(itemTag)
     * @param craft CraftMetaItem
     * @param nbtTag NBTTagCompound
     */
    @JvmStatic
    fun applyToItem(craft: Any, nbtTag: Any) {
        craft.invokeMethod<Void>("applyToItem", nbtTag)
    }

}