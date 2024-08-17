@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.nms

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.impl.SimpleMaterial.Companion.asBukkit
import cn.fd.ratziel.module.item.impl.SimpleMaterial.Companion.unsafeId
import cn.fd.ratziel.module.nbt.NBTCompound
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
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
    private var handle: BukkitItemStack = when {
        isObcClass(raw::class.java) -> raw // CraftItemStack
        isNmsClass(raw::class.java) -> newObc(raw) // net.minecraft.world.item.ItemStack
        raw is BukkitItemStack -> newObc(raw) // an impl of interface BukkitItemStack, but not CraftItemStack
        else -> throw UnsupportedTypeException(raw) // Unsupported Type
    } as? BukkitItemStack ?: throw UnsupportedTypeException(raw)

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
    fun setCustomTag(tag: NBTCompound) {
        NMSItem.INSTANCE.setCustomTag(getAsNms() ?: return, tag)
    }

    /**
     * 获取物品数据
     */
    fun getData(): ItemData {
        return SimpleData(
            material = SimpleMaterial(material),
            tag = getTag() ?: NBTCompound(),
            amount = amount
        )
    }

    /**
     * 设置物品数据 [ItemData]
     */
    fun setData(data: ItemData) {
        this.material = data.material.asBukkit()
        this.amount = data.amount
        setTag(data.tag)
    }

    /**
     * 物品材料
     */
    var material: BukkitMaterial
        get() {
            return if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) handle.type
            else SimpleMaterial.findBukkit(InternalUtil.obcGetMaterialMethodLegacy.invoke(handle)!! as Int)!!
        }
        set(value) {
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) {
                handle.type = value
            } else {
                InternalUtil.obcSetMaterialMethodLegacy.invoke(handle, material.unsafeId)
            }
        }

    /**
     * 物品数量
     */
    var amount: Int
        get() = handle.amount
        set(value) {
            handle.amount = value
        }

    /**
     * 物品最大堆叠数量
     */
    val maxStackSize: Int get() = handle.maxStackSize

    /**
     * 物品损伤值
     */
    var damage: Short
        get() = handle.durability
        set(value) {
            handle.durability = value
        }

    /**
     * 克隆数据
     */
    fun clone() = RefItemStack(InternalUtil.obcCloneMethod.invoke(handle)!!)

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
    fun getAsBukkit(): BukkitItemStack = handle

    companion object {

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        val nmsClass by lazy { nmsClass("ItemStack") }

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
        fun asNMSCopy(original: BukkitItemStack): Any = InternalUtil.asNMSCopyMethod.invokeStatic(original)!!

        /**
         * public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original)
         */
        fun asBukkitCopy(original: Any): BukkitItemStack = InternalUtil.asBukkitCopyMethod.invokeStatic(original)!! as BukkitItemStack

        /**
         * public static CraftItemStack asCraftCopy(ItemStack original)
         */
        fun asCraftCopy(original: BukkitItemStack): Any = InternalUtil.asCraftCopyMethod.invokeStatic(original)!!

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

        val asNMSCopyMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("asNMSCopy", BukkitItemStack::class.java)
        }

        val asBukkitCopyMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("asBukkitCopy", nmsClass)
        }

        val asCraftCopyMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("asCraftCopy", BukkitItemStack::class.java)
        }

        // net.minecraft.world.item.ItemStack handle;
        val obcHandleField by lazy {
            ReflexClass.of(obcClass).structure.getField("handle")
        }

        // public CraftItemStack clone()
        val obcCloneMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("clone")
        }

        // public int getTypeId()
        val obcGetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getTypeId")
        }

        // public void setTypeId(int var1)
        val obcSetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setTypeId", Int::class.java)
        }

    }

}