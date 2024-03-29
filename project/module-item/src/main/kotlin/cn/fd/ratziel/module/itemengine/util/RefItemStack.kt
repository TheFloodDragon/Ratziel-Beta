package cn.fd.ratziel.module.itemengine.util

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.core.function.getFieldUnsafe
import cn.fd.ratziel.core.function.getMethodUnsafe
import cn.fd.ratziel.core.function.isAssignableTo
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
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
 * @since 2023/10/28 16:18
 */
open class RefItemStack(rawData: Any) {

    protected open var data: Any =
        if (isOBC(rawData) || isNMS(rawData)) rawData // CraftItemStack or nms.ItemStack
        else if (rawData is ItemStack) newObc(rawData) // interface bukkit.ItemStack
        else throw UnsupportedTypeException(rawData) // Unsupported Type

    /**
     * 获取物品NBT标签
     */
    open fun getNBT() = this.getAsNMS()?.let { nmsTagField.get(it) }?.let { NBTCompound(it) }

    /**
     * 设置物品NBT标签
     */
    open fun setNBT(nbt: NBTCompound) = this.getAsNMS()?.let { nmsTagField.set(it, nbt.getAsNmsNBT()) }

    /**
     * 克隆数据
     */
    open fun clone() = this.apply {
        this.data = if (isOBC()) this.data.invokeMethod("clone")!! else nmsCloneMethod.invoke(this.data)!!
    }

    /**
     * 获取NMS形式
     * CraftItemStack: nms.ItemStack handle
     */
    open fun getAsNMS() = if (isOBC()) data.getProperty("handle") else data

    /**
     * 获取OBC形式
     */
    open fun getAsOBC() = if (isNMS()) newObc(data) else data

    /**
     * 数据类型判断方法
     */
    open fun isOBC() = isOBC(data)
    open fun isNMS() = isNMS(data)

    protected fun isOBC(target: Any) = target::class.java.isAssignableTo(obcClass)
    protected fun isNMS(target: Any) = target::class.java.isAssignableTo(nmsClass)

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
            ReflexClass.of(nmsClass).structure.getFieldUnsafe(
                name = if (MinecraftVersion.isUniversal) "v" else "tag",
                type = NBTCompound.clazz
            )
        }

        // public nms.ItemStack p()
        // public nms.ItemStack cloneItemStack()
        internal val nmsCloneMethod by lazy {
            ReflexClass.of(nmsClass).structure.getMethodUnsafe(
                name = if (MinecraftVersion.isUniversal) "p" else "cloneItemStack",
                returnType = nmsClass
            )
        }

    }

}