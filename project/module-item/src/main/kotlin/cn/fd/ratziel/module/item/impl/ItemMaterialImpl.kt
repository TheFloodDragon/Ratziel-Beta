@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.exception.UnknownMaterialException
import taboolib.library.reflex.ReflexClass
import taboolib.library.xseries.XMaterial

/**
 * ItemMaterialImpl
 *
 * 由于不可抗力的影响(我不会), 仅支持 [BukkitMaterial], 即仅支持原版物品
 *
 * @author TheFloodDragon
 * @since 2024/4/5 13:26
 */
data class ItemMaterialImpl(override val name: String) : ItemMaterial {

    constructor(mat: XMaterial) : this(mat.name)

    constructor(mat: BukkitMaterial) : this(mat.name)

    constructor(mat: ItemMaterial) : this(mat.name)

    constructor(id: Int) : this(getBukkitMaterial(id) ?: BukkitMaterial.AIR)

    /**
     * 材料标识符 (低版本)
     */
    override val id: Int get() = getIdUnsafe(insBukkit)

    /**
     * 材料的默认最大堆叠数量
     */
    override val maxStackSize: Int get() = insBukkit.maxStackSize

    /**
     * 材料的默认最大耐久度
     */
    override val maxDurability: Int get() = insBukkit.maxDurability.toInt()

    /**
     * 材料是否为空气材料
     */
    fun isAir() = insBukkit.isAir || this.isEmpty()

    /**
     * 获取 [BukkitMaterial] 形式 (若获取不到则抛出异常)
     */
    fun getAsBukkit(): BukkitMaterial = insBukkit

    /**
     * 获取 [XMaterial] 形式 (若获取不到则抛出异常)
     */
    fun getAsXSeries(): XMaterial = insXSeries

    /**
     * [BukkitMaterial] 形式
     */
    private val insBukkit: BukkitMaterial by lazy { getBukkitMaterial(name) ?: throw UnknownMaterialException(name) }

    /**
     * [XMaterial] 形式
     */
    private val insXSeries: XMaterial by lazy { XMaterial.matchXMaterial(insBukkit) }

    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?) = equal(this, other)

    companion object {

        fun equal(material: ItemMaterial, other: Any?) = material === other
                || (other as? ItemMaterial)?.id == material.id
                || (other as? BukkitMaterial)?.let { getIdUnsafe(it) } == material.id
                || (other as? XMaterial)?.id == material.id

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
                BukkitMaterial.entries.forEach { put(it.name, ItemMaterialImpl(it)) }
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

        /**
         * 类型判断
         */

        fun isPotion(material: ItemMaterial) = material.name.contains("POTION", true)

        private val leatherArmors by lazy {
            arrayOf(
                BukkitMaterial.LEATHER_HELMET.name,
                BukkitMaterial.LEATHER_CHESTPLATE.name,
                BukkitMaterial.LEATHER_LEGGINGS.name,
                BukkitMaterial.LEATHER_BOOTS.name
            )
        }

        fun isLeatherArmor(material: ItemMaterial) = leatherArmors.contains(material.name.uppercase())

    }

}

typealias BukkitMaterial = org.bukkit.Material