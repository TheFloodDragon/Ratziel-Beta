package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass

/**
 * RefItemStack
 *
 * @author TheFloodDragon
 * @since 2024/3/23 12:50
 */
class RefItemStack(rawData: Any) {

    /**
     * ItemStack源类型
     */
    private var source: Any = when {
        isObcClass(rawData) || isNmsClass(rawData) -> rawData // CraftItemStack or nms.ItemStack
        rawData is ItemStack -> newObc(rawData) // interface bukkit.ItemStack
        else -> throw UnsupportedTypeException(rawData) // Unsupported Type
    }

    /**
     * 获取物品NBT数据
     */
    fun getData() = this.getAsNms()?.let { nmsTagField.get(it) }?.let { NBTCompound(it) }

    /**
     * 获取物品NBT数据
     */
    fun setData(nbt: NBTCompound) = this.getAsNms()?.let { nmsTagField.set(it, nbt.getData()) }

    /**
     * 克隆数据
     */
    fun clone() = this.apply {
        this.source = if (isObcClass(source)) source.invokeMethod("clone")!! else nmsCloneMethod.invoke(source)!!
    }

    /**
     * 获取NMS形式实例
     * CraftItemStack: nms.ItemStack handle
     */
    fun getAsNms() = if (isObcClass(source)) source.getProperty("handle") else source

    /**
     * 获取OBC形式实例
     */
    fun getAsObc() = if (isNmsClass(source)) newObc(source).also { source = it } else source

    companion object {

        /**
         * obc.ItemStack
         *   org.bukkit.craftbukkit.$VERSION.inventory.CraftItemStack
         */
        @JvmStatic
        val obcClass by lazy { obcClass("inventory.CraftItemStack") }

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        @JvmStatic
        val nmsClass by lazy { nmsClass("ItemStack") }

        @JvmStatic
        fun newObc() = obcClass.invokeConstructor()

        // private CraftItemStack(nms.ItemStack item)
        // private CraftItemStack(bukkit.ItemStack item)
        @JvmStatic
        fun newObc(item: Any) = obcClass.invokeConstructor(item)

        @JvmStatic
        fun newNms() = nmsClass.invokeConstructor()

        // private nms.ItemStack(NBTTagCompound nbt)
        @JvmStatic
        fun newNms(nbt: Any) = nmsClass.invokeConstructor(nbt)

        // private NBTTagCompound v
        // private NBTTagCompound tag
        internal val nmsTagField by lazy {
            ReflexClass.of(nmsClass).structure.getField(
                if (MinecraftVersion.isUniversal) "v" else "tag"
            )
        }

        // public nms.ItemStack p()
        // public nms.ItemStack cloneItemStack()
        internal val nmsCloneMethod by lazy {
            ReflexClass.of(nmsClass).structure.getMethod(
                if (MinecraftVersion.isUniversal) "p" else "cloneItemStack"
            )
        }

        fun isObcClass(target: Any) = obcClass.isAssignableFrom(target::class.java)

        fun isNmsClass(target: Any) = nmsClass.isAssignableFrom(target::class.java)

    }

}