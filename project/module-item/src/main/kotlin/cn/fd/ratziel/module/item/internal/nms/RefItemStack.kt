@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.impl.SimpleMaterial.Companion.toBukkit
import cn.fd.ratziel.module.item.internal.nms.RefItemStack.Companion.nmsClass
import cn.fd.ratziel.module.item.internal.nms.RefItemStack.Companion.obcClass
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.library.reflex.ReflexClass
import taboolib.library.xseries.XMaterial
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
class RefItemStack private constructor(
    /**
     * ItemStack处理对象 (CraftItemStack)
     * 确保 CraftItemStack.handle 不为空
     */
    private var handle: BukkitItemStack,
) : ItemData {

    private constructor() : this(newObc() as BukkitItemStack)

    /**
     * 物品总标签数据
     * @see NMSItem.getTag
     * @see NMSItem.setTag
     */
    override var tag: NbtCompound
        get() {
            return nmsStack?.let { NMSItem.INSTANCE.getTag(it) } ?: NbtCompound()
        }
        set(value) {
            NMSItem.INSTANCE.setTag(nmsStack ?: return, value)
        }

    /**
     * 物品材料
     */
    override var material: ItemMaterial
        get() {
            return if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) SimpleMaterial(handle.type)
            else SimpleMaterial.findBy(InternalUtil.obcGetMaterialMethodLegacy.invoke(handle)!! as Int)!!
        }
        set(value) {
            if (MinecraftVersion.isHigherOrEqual(MinecraftVersion.V1_13)) {
                handle.type = value.toBukkit()
            } else {
                InternalUtil.obcSetMaterialMethodLegacy.invoke(handle, material.id)
            }
        }

    /**
     * 物品数量
     */
    override var amount: Int
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
    override fun clone() = RefItemStack(InternalUtil.obcCloneMethod.invoke(handle) as BukkitItemStack)

    /**
     * 获取NMS形式实例 [net.minecraft.world.item.ItemStack]
     */
    val nmsStack: Any? get() = InternalUtil.obcHandleField.get(handle)

    /**
     * Bukkit形式实例 ([BukkitItemStack])
     */
    val bukkitStack: BukkitItemStack get() = handle

    companion object {

        /**
         * 创建一个材质为 [material] 的 [RefItemStack]
         */
        @JvmStatic
        fun of(material: ItemMaterial) = RefItemStack().also { it.material = material }

        /**
         * 创建一个材质为 [material] 的 [RefItemStack]
         */
        @JvmStatic
        fun of(material: BukkitMaterial) = of(SimpleMaterial(material))

        /**
         * 创建一个材质为 [material] 的 [RefItemStack]
         */
        @JvmStatic
        fun of(material: XMaterial) = of(SimpleMaterial(material))

        /**
         * 通过 [ItemData] 创建一个 [RefItemStack]
         *
         * @param copyTag 是否复制标签 (默认不复制)
         */
        @JvmStatic
        fun of(data: ItemData, copyTag: Boolean = false) = RefItemStack().apply {
            this.material = data.material
            this.tag = if (copyTag) data.tag.clone() else data.tag
            this.amount = data.amount
        }

        /**
         * 通过 [BukkitItemStack] 创建一个 [RefItemStack]
         */
        @JvmStatic
        fun of(itemStack: BukkitItemStack) =
            if (isObcClass(itemStack::class.java)) {
                RefItemStack(itemStack)  // CraftItemStack
            } else RefItemStack(newObc(itemStack) as BukkitItemStack) // an impl of interface BukkitItemStack, but not CraftItemStack

        /**
         * 通过 [net.minecraft.world.item.ItemStack] 创建一个 [RefItemStack]
         */
        @JvmStatic
        fun ofNms(nmsItem: Any) = RefItemStack(newObc(nmsItem) as BukkitItemStack)

        /**
         * 从 [itemStack] 中提取 [ItemData]
         */
        fun exactData(itemStack: BukkitItemStack): ItemData {
            val ref = of(itemStack)
            return SimpleData(ref.material, ref.tag, ref.amount)
        }

        /**
         * nms.ItemStack
         *   1.17+ net.minecraft.world.item.ItemStack
         *   1.17- net.minecraft.server.$VERSION.ItemStack
         */
        @JvmStatic
        val nmsClass by lazy { nmsClass("ItemStack") }

        /**
         * obc.ItemStack
         *   org.bukkit.craftbukkit.$VERSION.inventory.CraftItemStack
         */
        @JvmStatic
        val obcClass by lazy { obcClass("inventory.CraftItemStack") }

        /**
         * private CraftItemStack(net.minecraft.world.item.ItemStack item) {
         *     this.handle = item;
         * }
         * 而在 [org.bukkit.Material] 为 AIR 时, handle 为 null,
         * 所以直接通过 [unsafeInstance] 构造了
         */
        @JvmStatic
        fun newObc() = obcClass.unsafeInstance()

        /**
         * private CraftItemStack(net.minecraft.world.item.ItemStack item)
         * private CraftItemStack(ItemStack item)
         */
        @JvmStatic
        fun newObc(nmsItem: Any) = obcClass.invokeConstructor(nmsItem)

        /**
         * 创建一个空的 [net.minecraft.world.item.ItemStack]
         * private ItemStack(@Nullable Void void_)
         */
        @JvmStatic
        fun newNms() = InternalUtil.nmsItemStackEmptyConstructor.instance(null)!!

        /**
         * public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original)
         */
        @JvmStatic
        fun asNMSCopy(original: BukkitItemStack): Any = InternalUtil.asNMSCopyMethod.invokeStatic(original)!!

        /**
         * public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original)
         */
        @JvmStatic
        fun asBukkitCopy(original: Any): BukkitItemStack = InternalUtil.asBukkitCopyMethod.invokeStatic(original)!! as BukkitItemStack

        /**
         * public static CraftItemStack asCraftCopy(ItemStack original)
         */
        @JvmStatic
        fun asCraftCopy(original: BukkitItemStack): Any = InternalUtil.asCraftCopyMethod.invokeStatic(original)!!

        /**
         * 检查类是否为[obcClass]
         */
        @JvmStatic
        fun isObcClass(clazz: Class<*>) = obcClass.isAssignableFrom(clazz)

        /**
         * 检查类是否为[nmsClass]
         */
        @JvmStatic
        fun isNmsClass(clazz: Class<*>) = nmsClass.isAssignableFrom(clazz)

    }

    private object InternalUtil {

        @JvmStatic
        val nmsItemStackEmptyConstructor by lazy {
            ReflexClass.of(nmsClass).structure.getConstructorByType(Void::class.java)
        }

        @JvmStatic
        val asNMSCopyMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("asNMSCopy", BukkitItemStack::class.java)
        }

        @JvmStatic
        val asBukkitCopyMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("asBukkitCopy", nmsClass)
        }

        @JvmStatic
        val asCraftCopyMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("asCraftCopy", BukkitItemStack::class.java)
        }

        // net.minecraft.world.item.ItemStack handle;
        @JvmStatic
        val obcHandleField by lazy {
            ReflexClass.of(obcClass).structure.getField("handle")
        }

        // public CraftItemStack clone()
        @JvmStatic
        val obcCloneMethod by lazy {
            ReflexClass.of(obcClass).structure.getMethod("clone")
        }

        // public int getTypeId()
        @JvmStatic
        val obcGetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethod("getTypeId")
        }

        // public void setTypeId(int var1)
        @JvmStatic
        val obcSetMaterialMethodLegacy by lazy {
            ReflexClass.of(obcClass).structure.getMethodByType("setTypeId", Int::class.java)
        }

    }

}