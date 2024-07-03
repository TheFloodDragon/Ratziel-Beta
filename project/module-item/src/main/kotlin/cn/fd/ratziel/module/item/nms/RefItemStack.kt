package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.ItemDataImpl
import cn.fd.ratziel.module.item.impl.ItemMaterialImpl
import cn.fd.ratziel.module.item.nbt.NBTCompound
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
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
class RefItemStack(raw: Any) {

    constructor() : this(newObc())

    constructor(data: ItemData) : this() {
        setData(data)
    }

    /**
     * ItemStack处理对象 (CraftItemStack)
     * 确保 CraftItemStack.handle 不为空
     */
    private var handle: Any = when {
        isObcClass(raw::class.java) -> raw // CraftItemStack
        isNmsClass(raw::class.java) -> newObc(raw) // net.minecraft.world.item.ItemStack
        BukkitItemStack::class.java.isAssignableFrom(raw::class.java) -> newObc(raw) // an impl of interface BukkitItemStack
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    }

    /**
     * 获取物品NBT标签 [NBTCompound]
     * @see NMSItem.getTag
     */
    fun getTag(): NBTCompound? {
        return NMSItem.INSTANCE.getTag(getAsNms() ?: return null)
    }

    /**
     * 设置物品NBT标签 [NBTCompound]
     * @see NMSItem.setTag
     */
    fun setTag(data: NBTCompound) {
        NMSItem.INSTANCE.setTag(getAsNms() ?: return, data)
    }

    /**
     * 获取物品自定义NBT标签 [NBTCompound]
     * @see NMSItem.getCustomTag
     */
    fun getCustomTag(): NBTCompound? {
        return NMSItem.INSTANCE.getCustomTag(getAsNms() ?: return null)
    }

    /**
     * 设置物品自定义NBT标签 [NBTCompound]
     * @see NMSItem.setCustomTag
     */
    fun setCustomTag(data: NBTCompound) {
        NMSItem.INSTANCE.setCustomTag(getAsNms() ?: return, data)
    }

    /**
     * 获取物品数据
     */
    fun getData(): ItemData? {
        return ItemDataImpl(
            material = ItemMaterialImpl(getMaterial()),
            tag = getTag() ?: return null,
            amount = getAmount()
        )
    }

    /**
     * 设置物品数据 [ItemData]
     */
    fun setData(data: ItemData) {
        setMaterial(data.material)
        setAmount(data.amount)
        setTag(data.tag)
    }

    /**
     * 获取物品材料
     */
    fun getMaterial(): BukkitMaterial =
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) {
            InternalUtil.obcGetMaterialMethod.invoke(handle)!! as BukkitMaterial
        } else {
            ItemMaterialImpl.getBukkitMaterial(InternalUtil.obcGetMaterialMethodLegacy.invoke(handle)!! as Int)!!
        }

    /**
     * 设置物品材料
     */
    fun setMaterial(material: BukkitMaterial) {
        if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) {
            InternalUtil.obcSetMaterialMethod.invoke(handle, material)
        } else {
            InternalUtil.obcSetMaterialMethodLegacy.invoke(handle, ItemMaterialImpl.getIdUnsafe(material))
        }
    }

    fun setMaterial(material: ItemMaterial) {
        ItemMaterialImpl.getBukkitMaterial(material.name)?.let { setMaterial(it) }
    }

    /**
     * 获取物品数量
     */
    fun getAmount(): Int = InternalUtil.obcGetAmountMethod.invoke(handle) as Int

    /**
     * 设置物品数量
     */
    fun setAmount(amount: Int) = InternalUtil.obcSetAmountMethod.invoke(handle, amount)

    /**
     * 获取物品最大堆叠数量
     */
    fun getMaxStackSize(): Int = InternalUtil.obcGetMaxStackSizeMethod.invoke(handle) as Int

    /**
     * 获取物品损伤值
     * @return [Short]
     */
    fun getDamage(): Short = InternalUtil.obcGetDamageMethod.invoke(handle) as Short

    /**
     * 设置物品损伤值
     */
    fun setDamage(damage: Short) = InternalUtil.obcSetDamageMethod.invoke(handle, damage)

    /**
     * 克隆数据
     */
    fun clone() = this.apply { this.handle = InternalUtil.obcCloneMethod.invoke(handle)!! }

    /**
     * 获取NMS形式实例 (net.minecraft.world.ItemStack)
     */
    fun getAsNms(): Any? = InternalUtil.obcHandleField.get(handle)

    /**
     * 获取CraftBukkit形式实例 (CraftItemStack)
     */
    fun getAsObc(): Any = handle

    /**
     * 获取Bukkit形式实例 ([BukkitItemStack])
     */
    fun getAsBukkit(): BukkitItemStack = getAsObc() as BukkitItemStack

    companion object {

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        val nmsClass by lazy { nmsClass("ItemStack") }

        fun newNms() = nmsClass.invokeConstructor()

        /**
         * private nms.ItemStack(NBTTagCompound nbt)
         */
        fun newNms(nbt: Any) = nmsClass.invokeConstructor(nbt)

        /**
         * obc.ItemStack
         *   org.bukkit.craftbukkit.$VERSION.inventory.CraftItemStack
         */
        val obcClass by lazy { obcClass("inventory.CraftItemStack") }

        /**
         * private CraftItemStack(net.minecraft.world.item.ItemStack item) {
         *     this.handle = item;
         * }
         * 而在 [BukkitMaterial] 为 AIR 时, handle 为 null,
         * 所以直接通过 [unsafeInstance] 构造了
         */
        fun newObc() = obcClass.unsafeInstance()

        /**
         * private CraftItemStack(net.minecraft.world.item.ItemStack item)
         * private CraftItemStack(ItemStack item)
         */
        fun newObc(nmsItem: Any) = obcClass.invokeConstructor(nmsItem)

        /**
         * public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original)
         */
        fun asNMSCopy(original: Any): Any = obcClass.invokeMethod("asNMSCopy", original, isStatic = true)!!

        /**
         * public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original)
         */
        fun asBukkitCopy(original: Any): BukkitItemStack = obcClass.invokeMethod("asBukkitCopy", original, isStatic = true)!!

        /**
         * public static CraftItemStack asCraftCopy(ItemStack original)
         */
        fun asCraftCopy(original: Any): Any = obcClass.invokeMethod("asCraftCopy", original, isStatic = true)!!

        /**
         * 检查类是否为[obcClass]
         */
        fun isObcClass(clazz: Class<*>) = obcClass.isAssignableFrom(clazz)

        /**
         * 检查类是否为[nmsClass]
         */
        fun isNmsClass(clazz: Class<*>) = nmsClass.isAssignableFrom(clazz)

    }

    internal object InternalUtil {

        /**
         * private NBTTagCompound A
         * private NBTTagCompound tag
         */
        val nmsTagField by lazy {
            ReflexClass.of(nmsClass).structure.getField(
                if (MinecraftVersion.isUniversal) "A" else "tag"
            )
        }

        /**
         * public nms.ItemStack p()
         * public nms.ItemStack cloneItemStack()
         * public ItemStack s()
         */
        val nmsCloneMethod by lazy {
            ReflexClass.of(nmsClass).structure.getMethodByType(
                if (MinecraftVersion.majorLegacy >= 12005) "s"
                else if (MinecraftVersion.isUniversal) "p"
                else "cloneItemStack"
            )
        }

        // net.minecraft.world.item.ItemStack handle;
        val obcHandleField by lazy {
            ReflexClass.of(obcClass).structure.getField("handle")
        }

        // public CraftItemStack clone()
        val obcCloneMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("clone")
        }

        // public Material getType()
        // public int getTypeId()
        val obcGetMaterialMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getType")
        }

        val obcGetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getTypeId")
        }

        // public void setType(Material type)
        // public void setTypeId(int var1)
        val obcSetMaterialMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setType", BukkitMaterial::class.java)
        }

        val obcSetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setTypeId", Int::class.java)
        }

        // public int getAmount()
        val obcGetAmountMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getAmount")
        }

        // public void setAmount(int var1)
        val obcSetAmountMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setAmount", Int::class.java)
        }

        // public int getMaxStackSize()
        val obcGetMaxStackSizeMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getMaxStackSize")
        }

        // public short getDurability()
        val obcGetDamageMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getDurability")
        }

        // public void setDurability(short var1)
        val obcSetDamageMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setDurability", Short::class.java)
        }

    }

}