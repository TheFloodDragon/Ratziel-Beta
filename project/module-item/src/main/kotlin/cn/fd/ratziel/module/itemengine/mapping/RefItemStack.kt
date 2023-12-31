package cn.fd.ratziel.module.itemengine.mapping

import cn.fd.ratziel.core.function.MirrorClass
import cn.fd.ratziel.core.function.UnsupportedTypeException
import cn.fd.ratziel.core.function.getFieldUnsafe
import cn.fd.ratziel.core.function.getMethodUnsafe
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
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
class RefItemStack(protected var data: Any) {

    // 类型检查
    init {
        if (!isOBC() && !isNMS()) throw UnsupportedTypeException(data)
    }

    /**
     * 获取物品NBT标签
     */
    fun getNBT() = this.getAsNMS()?.let { nmsGetTagMethod.get(it) }?.let { NBTCompound(it) }

    /**
     * 设置物品NBT标签
     */
    fun setNBT(nbt: NBTCompound) = this.getAsNMS()?.let { nmsGetTagMethod.set(it, nbt.getAsNmsNBT()) }

    /**
     * 克隆数据
     */
    fun clone() = this.apply {
        this.data = if (isOBC()) this.data.invokeMethod("clone")!! else nmsCloneMethod.invoke(this.data)!!
    }

    /**
     * 获取NMS形式
     */
    fun getAsNMS() = if (isOBC()) data.getProperty("handle") else data

    /**
     * 数据类型判断方法
     */
    fun isOBC() = data::class.java.isAssignableFrom(clazz)
    fun isNMS() = data::class.java.isAssignableFrom(nmsClass)

    companion object : MirrorClass<RefItemStack>() {

        // org.bukkit.craftbukkit.$VERSION.inventory.CraftItemStack
        @JvmStatic
        override val clazz by lazy { obcClass("inventory.CraftItemStack") }

        @JvmStatic
        override fun of(obj: Any) = RefItemStack(obj)

        /**
         * (nms)ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        @JvmStatic
        val nmsClass by lazy { nmsClass("ItemStack") }

        fun newOBC() = clazz.invokeConstructor()

        // private CraftItemStack(net.minecraft.world.item.ItemStack item)
        fun newOBC(nms: Any) = clazz.invokeConstructor(nms)

        fun newNMS() = nmsClass.invokeConstructor()

        // private ItemStack(NBTTagCompound nbt)
        fun newNMS(nbt: Any) = nmsClass.invokeConstructor(nbt)

        // private NBTTagCompound v
        // private NBTTagCompound tag
        internal val nmsGetTagMethod by lazy {
            ReflexClass.of(nmsClass).structure.getFieldUnsafe(
                name = if (MinecraftVersion.isUniversal) "v" else "tag",
                type = NBTCompound.clazz
            )
        }

        // public ItemStack p()
        // public ItemStack cloneItemStack()
        internal val nmsCloneMethod by lazy {
            ReflexClass.of(nmsClass).structure.getMethodUnsafe(
                name = if (MinecraftVersion.isUniversal) "p" else "cloneItemStack",
                returnType = nmsClass
            )
        }

    }

}