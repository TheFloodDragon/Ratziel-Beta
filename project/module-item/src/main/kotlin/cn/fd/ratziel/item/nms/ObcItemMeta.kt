package cn.fd.ratziel.item.nms

import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass

/**
 * ObcItemMeta
 *
 * @author TheFloodDragon
 * @since 2023/10/22 10:14
 */
object ObcItemMeta {

    /**
     * NBTTagCompound
     *   1.17+ net.minecraft.nbt.NBTTagCompound
     *   1.16- net.minecraft.server.v1_16_R3.NBTTagCompound
     */
    @JvmStatic
    val nbtTagClass by lazy {
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_17))
            Class.forName("net.minecraft.nbt.NBTTagCompound")
        else nmsClass("NBTTagCompound")
    }

    @JvmStatic
    val metaClass by lazy {
        obcClass("inventory.CraftMetaItem")
    }

    /**
     * CraftMetaItem#constructor(itemTag)
     * @param tag NBTTagCompound
     * @return CraftMetaItem
     */
    @JvmStatic
    fun build(tag: Any) =
        metaClass.getConstructor(nbtTagClass).newInstance(tag)

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