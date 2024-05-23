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
    override val id: Int get() = getIdUnsafe(bukkitMaterial)

    /**
     * 材料的默认最大堆叠数量
     */
    override val maxStackSize: Int get() = bukkitMaterial.maxStackSize

    /**
     * 材料的默认最大耐久度
     */
    override val maxDurability: Int get() = bukkitMaterial.maxDurability.toInt()

    /**
     * 材料是否为空气材料
     */
    fun isAir() = bukkitMaterial.isAir || ItemMaterial.isEmpty(this)

    /**
     * [BukkitMaterial] 形式 (若获取不到则抛出异常)
     */
    val bukkitMaterial: BukkitMaterial by lazy { getBukkitMaterial(name) ?: throw UnknownMaterialException(name) }

    /**
     * [XMaterial] 形式 (若获取不到则抛出异常)
     */
    val xseriesMaterial: XMaterial by lazy { XMaterial.matchXMaterial(bukkitMaterial) }

    companion object {

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
         * 通过反射获取 [id], 因为 [BukkitMaterial.getId] 不会获取老版物品的 [id]
         */
        fun getIdUnsafe(material: BukkitMaterial) = bukkitIdField.get(material) as Int

        /**
         * private final int id
         */
        internal val bukkitIdField by lazy {
            ReflexClass.of(BukkitMaterial::class.java, false).structure.getField("id")
        }

    }

}

typealias BukkitMaterial = org.bukkit.Material