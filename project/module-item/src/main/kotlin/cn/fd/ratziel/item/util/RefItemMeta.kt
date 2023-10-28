package cn.fd.ratziel.item.util

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
    fun createInstance(tag: Any) = metaClass.invokeConstructor(tag)

    /**
     * CraftMetaItem#applyToItem(itemTag)
     * @param craft CraftMetaItem
     * @param itemTag NBTTagCompound
     */
    @JvmStatic
    fun applyToItem(craft: Any, itemTag: Any) {
        craft.invokeMethod<Void>("applyToItem", itemTag)
    }

}