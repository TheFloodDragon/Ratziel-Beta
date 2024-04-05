package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.reflex.RefItemStack.Companion.obcClass
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import org.bukkit.inventory.ItemStack as BukkitItemStack

/**
 * RefItemStack
 *
 * @author TheFloodDragon
 * @since 2024/4/4 11:20
 */
class RefItemStack(rawData: Any) {

    /**
     * ItemStack的NMS处理对象
     */
    private var handle: Any? = when {
        isNmsClass(rawData::class.java) -> rawData // nms.ItemStack
        isObcClass(rawData::class.java) -> getNmsFrom(rawData) // CraftItemStack
        BukkitItemStack::class.java.isAssignableFrom(rawData::class.java) -> newObc(rawData) // an impl of interface bukkit.ItemStack
        else -> throw UnsupportedTypeException(rawData) // Unsupported Type
    }

    private val craftHandle: Any? by lazy { handle?.let { newObc(it) } }

    /**
     * 获取物品NBT数据
     */
    fun getData(): NBTCompound? = handle?.let { nmsTagField.get(it) }?.let { NBTCompound(it) }

    /**
     * 获取物品NBT数据
     */
    fun setData(nbt: NBTCompound) {
        if (handle != null) nmsTagField.set(handle, nbt.getData())
    }

    /**
     * 克隆数据
     */
    fun clone(): RefItemStack = this.apply { this.handle = handle?.let { nmsCloneMethod.invoke(it) } }

    /**
     * 获取NMS形式实例
     * CraftItemStack: nms.ItemStack handle
     */
    fun getAsNms() = handle

    /**
     * 获取OBC形式实例
     */
    fun getAsObc() = craftHandle

    companion object {

        /**
         * obc.ItemStack
         *   org.bukkit.craftbukkit.$VERSION.inventory.CraftItemStack
         */
        val obcClass by lazy { obcClass("inventory.CraftItemStack") }

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        val nmsClass by lazy { nmsClass("ItemStack") }

        fun newObc() = obcClass.invokeConstructor()

        // private CraftItemStack(nms.ItemStack item)
        // private CraftItemStack(bukkit.ItemStack item)
        fun newObc(nmsItem: Any) = obcClass.invokeConstructor(nmsItem)

        fun newNms() = nmsClass.invokeConstructor()

        // private nms.ItemStack(NBTTagCompound nbt)
        fun newNms(nbt: Any) = nmsClass.invokeConstructor(nbt)

        /**
         * 从[obcClass]中获取[nmsClass]实例
         */
        fun getNmsFrom(obcItem: Any) = obcHandleField.get(obcItem)

        /**
         * 检查类是否为[obcClass]
         */
        fun isObcClass(clazz: Class<*>) = obcClass.isAssignableFrom(clazz)

        /**
         * 检查类是否为[nmsClass]
         */
        fun isNmsClass(clazz: Class<*>) = nmsClass.isAssignableFrom(clazz)

        // private NBTTagCompound A
        // private NBTTagCompound tag
        internal val nmsTagField by lazy {
            ReflexClass.of(nmsClass).structure.getField(
                if (MinecraftVersion.isUniversal) "A" else "tag"
            )
        }

        // public nms.ItemStack p()
        // public nms.ItemStack cloneItemStack()
        internal val nmsCloneMethod by lazy {
            ReflexClass.of(nmsClass).structure.getMethod(
                if (MinecraftVersion.isUniversal) "p" else "cloneItemStack"
            )
        }

        // net.minecraft.world.item.ItemStack handle;
        internal val obcHandleField by lazy {
            ReflexClass.of(obcClass).structure.getField("handle")
        }

        // public CraftItemStack clone()
        internal val obcCloneMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("clone")
        }

    }

}