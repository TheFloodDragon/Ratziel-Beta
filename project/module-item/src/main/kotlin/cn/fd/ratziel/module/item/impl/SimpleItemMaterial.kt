package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemMaterial
import taboolib.library.reflex.ReflexClass
import taboolib.library.xseries.XMaterial

/**
 * SimpleItemMaterial
 *
 * 由于不可抗力的影响(我不会), 仅支持 [BukkitMaterial], 即仅支持原版物品
 *
 * @author TheFloodDragon
 * @since 2024/4/5 13:26
 */
open class SimpleItemMaterial(private val ref: BukkitMaterial) : ItemMaterial {

    constructor(name: String) : this(getBukkitMaterial(name) ?: BukkitMaterial.AIR)

    constructor(id: Int) : this(getBukkitMaterial(id) ?: BukkitMaterial.AIR)

    constructor(mat: XMaterial) : this(mat.name)

    constructor(mat: ItemMaterial) : this(mat.name)

    /**
     * 材料标识符 (低版本)
     */
    override val id: Int get() = getIdUnsafe(ref)

    /**
     * 材料名称
     */
    override val name: String get() = ref.name

    /**
     * 材料的默认最大堆叠数量
     */
    override val maxStackSize: Int get() = ref.maxStackSize

    /**
     * 材料的默认最大耐久度
     */
    override val maxDurability: Int get() = ref.maxDurability.toInt()

    /**
     * 材料是否为空气材料
     */
    open fun isAir() = ref.isAir || this.isEmpty()

    /**
     * 获取 [BukkitMaterial] 形式
     */
    open fun getAsBukkit(): BukkitMaterial = ref

    /**
     * 获取 [XMaterial] 形式
     */
    open fun getAsXSeries(): XMaterial = XMaterial.matchXMaterial(ref)

    override fun toString() = "SimpleItemMaterial(name=$name,id=$id)"

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?) = equals(this, other)

    companion object {

        fun equals(material: ItemMaterial, other: Any?) = material === other
                || (other as? ItemMaterial)?.name == material.name
                || (other as? BukkitMaterial)?.name == material.name
                || (other as? XMaterial)?.name == material.name

        /**
         * 获取 [BukkitMaterial] 形式的物品材料
         */
        fun getBukkitMaterial(name: String) = BukkitMaterial.getMaterial(name)

        fun getBukkitMaterial(id: Int) = BukkitMaterial.entries.find { getIdUnsafe(it) == id }

        /**
         * 材料大全
         */
        val materialsMap by lazy {
            HashMap<String, ItemMaterial>().apply {
                BukkitMaterial.entries.forEach { put(it.name, SimpleItemMaterial(it)) }
            }
        }

        /**
         * 通过反射获取 [id], 因为 [BukkitMaterial] 不会获取老版物品的 [id]
         */
        fun getIdUnsafe(material: BukkitMaterial) = bukkitIdField.get(material) as Int

        /**
         * private final int id
         */
        private val bukkitIdField by lazy {
            ReflexClass.of(BukkitMaterial::class.java, false).structure.getField("id")
        }

    }

}

typealias BukkitMaterial = org.bukkit.Material