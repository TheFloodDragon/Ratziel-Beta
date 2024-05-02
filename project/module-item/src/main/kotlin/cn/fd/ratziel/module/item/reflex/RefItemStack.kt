package cn.fd.ratziel.module.item.reflex

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.ItemMaterialImpl
import cn.fd.ratziel.module.item.impl.ItemMaterialImpl.Companion.getBukkitForm
import cn.fd.ratziel.module.item.impl.ItemMaterialImpl.Companion.getIdByReflex
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.reflex.NMSItem.Companion.nmsClass
import cn.fd.ratziel.module.item.reflex.RefItemStack.Companion.obcClass
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import org.bukkit.Material as BukkitMaterial
import org.bukkit.inventory.ItemStack as BukkitItemStack

/**
 * RefItemStack
 *
 * @author TheFloodDragon
 * @since 2024/4/4 11:20
 */
class RefItemStack(raw: Any) {

    /**
     * ItemStack的CraftBukkit处理对象
     */
    private var handle: Any? = when {
        isObcClass(raw::class.java) -> raw // CraftItemStack
        isNmsClass(raw::class.java) -> newObc(raw) // nms.ItemStack
        BukkitItemStack::class.java.isAssignableFrom(raw::class.java) -> newObc(raw) // an impl of interface bukkit.ItemStack
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    }

    /**
     * 获取物品NBT数据
     */
    fun getData(): NBTCompound? = getAsNms()?.let { NMSItem.instance.getItemNBT(it) }?.let { NBTCompound(it) }

    /**
     * 设置物品NBT数据
     */
    fun setData(data: NBTCompound) = getAsNms()?.let { NMSItem.instance.setItemNBT(it, data.getData()) }

    /**
     * 获取物品材料
     */
    fun getMaterial(): ItemMaterial = handle?.let { craft ->
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13))
            (obcGetMaterialMethod.invoke(craft) as? BukkitMaterial)
        else (obcGetMaterialMethodLegacy.invoke(craft) as? Int)?.let { getBukkitForm(it) }
    }?.let { ItemMaterialImpl(it) } ?: ItemMaterialImpl.AIR

    /**
     * 设置物品材料
     */
    fun setMaterial(material: BukkitMaterial) {
        handle?.let { craft ->
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) obcSetMaterialMethod.invoke(craft, material)
            else obcSetMaterialMethodLegacy.invoke(craft, material.getIdByReflex())
        }
    }

    /**
     * 获取物品数量
     */
    fun getAmount(): Int = handle?.let { obcGetAmountMethod.invoke(it) as? Int } ?: 0

    /**
     * 设置物品数量
     */
    fun setAmount(amount: Int) {
        handle?.let { obcSetAmountMethod.invoke(it, amount) }
    }

    /**
     * 获取物品最大堆叠数量
     */
    fun getMaxStackSize(): Int = handle?.let { obcGetMaxStackSizeMethod.invoke(it) as? Int } ?: 0

    /**
     * 获取物品损伤值
     */
    fun getDamage(): Short = handle?.let { obcGetDamageMethod.invoke(it) as? Short } ?: 0

    /**
     * 设置物品损伤值
     */
    fun setDamage(damage: Short) {
        handle?.let { obcSetDamageMethod.invoke(it, damage) }
    }

    /**
     * 克隆数据
     */
    fun clone() = this.apply { this.handle = handle?.let { obcCloneMethod.invoke(it) } }

    /**
     * 获取NMS形式实例
     */
    fun getAsNms() = handle?.let { getNmsFrom(it) }

    /**
     * 获取OBC形式实例
     */
    fun getAsObc() = handle

    companion object {

        /**
         * obc.ItemStack
         *   org.bukkit.craftbukkit.$VERSION.inventory.CraftItemStack
         */
        val obcClass by lazy { obcClass("inventory.CraftItemStack") }

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

        // net.minecraft.world.item.ItemStack handle;
        internal val obcHandleField by lazy {
            ReflexClass.of(obcClass).getField("handle")
        }

        // public CraftItemStack clone()
        internal val obcCloneMethod by lazy {
            ReflexClass.of(obcClass).getMethod("clone")
        }

        // public Material getType()
        // public int getTypeId()
        internal val obcGetMaterialMethod by lazy {
            ReflexClass.of(obcClass).getMethod("getType")
        }

        internal val obcGetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).getMethod("getTypeId")
        }

        // public void setType(Material type)
        // public void setTypeId(int var1)
        internal val obcSetMaterialMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setType", BukkitMaterial::class.java)
        }

        internal val obcSetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setTypeId", Int::class.java)
        }

        // public int getAmount()
        internal val obcGetAmountMethod by lazy {
            ReflexClass.of(obcClass).getMethod("getAmount")
        }

        // public void setAmount(int var1)
        internal val obcSetAmountMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setAmount", Int::class.java)
        }

        // public int getMaxStackSize()
        internal val obcGetMaxStackSizeMethod by lazy {
            ReflexClass.of(obcClass).getMethod("getMaxStackSize")
        }

        // public short getDurability()
        internal val obcGetDamageMethod by lazy {
            ReflexClass.of(obcClass).getMethod("getDurability")
        }

        // public void setDurability(short var1)
        internal val obcSetDamageMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setDurability", Short::class.java)
        }

    }

}