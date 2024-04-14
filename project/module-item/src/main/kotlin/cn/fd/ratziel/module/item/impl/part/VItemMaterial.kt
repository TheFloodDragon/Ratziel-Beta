package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.item.api.part.ItemMaterial
import cn.fd.ratziel.module.item.exception.UnknownMaterialException
import taboolib.library.reflex.ReflexClass
import taboolib.library.xseries.XMaterial
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Material as BukkitMaterial

/**
 * VItemMaterial
 *
 * 由于不可抗力的影响(我不会), 仅支持 [BukkitMaterial], 即仅支持原版物品
 *
 * @author TheFloodDragon
 * @since 2024/4/5 13:26
 */
data class VItemMaterial(override val name: String) : ItemMaterial {

    constructor(mat: XMaterial) : this(mat.name)

    constructor(matId: Int) : this(getBukkitForm(matId) ?: BukkitMaterial.AIR)

    constructor(mat: BukkitMaterial) : this(mat.name)

    constructor(mat: ItemMaterial) : this(mat.name)

    /**
     * 材料标识符 (低版本)
     */
    override val id: Int get() = bukkitForm.getIdByReflex()

    /**
     * 材料的默认最大堆叠数量
     */
    override val maxStackSize: Int get() = bukkitForm.maxStackSize

    /**
     * 材料的默认最大耐久度
     */
    override val maxDurability: Int get() = bukkitForm.maxDurability.toInt()

    /**
     * 材料是否为空气材料
     */
    val isAir: Boolean get() = bukkitForm.isAir

    /**
     * [BukkitMaterial] 形式 (若获取不到则抛出异常)
     */
    val bukkitForm: BukkitMaterial by lazy { getBukkitForm(name) ?: throw UnknownMaterialException(name) }

    /**
     * [XMaterial] 形式 (若获取不到则抛出异常)
     */
    val xseriesForm: XMaterial by lazy { XMaterial.matchXMaterial(bukkitForm) }

    companion object {

        val AIR by lazy { VItemMaterial(BukkitMaterial.AIR) }

        /**
         * 获取 [BukkitMaterial] 形式的物品材料
         */
        fun getBukkitForm(name: String) = BukkitMaterial.getMaterial(name)

        fun getBukkitForm(id: Int) = BukkitMaterial.entries.find { it.getIdByReflex() == id }

        /**
         * 材料大全
         */
        val materialsMap by lazy {
            ConcurrentHashMap<String, ItemMaterial>().apply {
                BukkitMaterial.entries.forEach { put(it.name, VItemMaterial(it.name)) }
            }
        }

        /**
         * 通过反射获取 [id], 因为 [BukkitMaterial.getId] 不会获取老版物品的 [id]
         */
        internal fun BukkitMaterial.getIdByReflex() = bukkitIdField.get(this) as Int

        // private final int id
        internal val bukkitIdField by lazy {
            ReflexClass.of(BukkitMaterial::class.java, false).structure.getField("id")
        }

    }

}