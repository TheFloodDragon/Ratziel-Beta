package cn.fd.ratziel.module.item.impl.part

import cn.fd.ratziel.module.itemengine.api.exception.UnknownMaterialException
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.xseries.XMaterial
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Material as BukkitMaterial

/**
 * ItemMaterial - 物品材料
 *
 * 由于不可抗力的影响(我不会), 仅支持 [BukkitMaterial], 即仅支持原版物品
 *
 * @author TheFloodDragon
 * @since 2024/1/28 12:49
 */
data class ItemMaterial(
    /**
     * 材料代号
     */
    val id: Int,
) {

    /**
     * 通过 [BukkitMaterial] 构造
     */
    constructor(bukkitImpl: BukkitMaterial) : this(bukkitImpl.safeId)

    /**
     * 通过 [name] 构造
     */
    constructor(name: String) : this((getBukkitForm(name) ?: throw UnknownMaterialException()).safeId)

    /**
     * 材料名称
     */
    val name: String get() = bukkitForm.name

    /**
     * 最大堆叠数量
     */
    val maxStackSize: Int get() = bukkitForm.maxStackSize

    /**
     * 最大耐久度
     */
    val maxDurability: Short get() = bukkitForm.maxDurability

    /**
     * 物品是否为空气方块
     */
    val isAir get() = bukkitForm.isAir

    /**
     * [BukkitMaterial] 形式 (若获取不到则抛出异常)
     */
    val bukkitForm: BukkitMaterial = getBukkitForm(id) ?: throw UnknownMaterialException(id)

    /**
     * [XMaterial] 形式 (若获取不到则抛出异常)
     */
    val xseriesForm: XMaterial get() = XMaterial.matchXMaterial(bukkitForm)

    companion object {

        /**
         * 获取 [BukkitMaterial] 形式的物品材料
         */
        fun getBukkitForm(name: String) = BukkitMaterial.getMaterial(name)

        fun getBukkitForm(id: Int) = BukkitMaterial.entries.find { it.safeId == id }

        val materialsMap by lazy {
            ConcurrentHashMap<String, ItemMaterial>().apply {
                BukkitMaterial.entries.forEach { put(it.name, ItemMaterial(it)) }
            }
        }

        /**
         * 通过反射获取 [id], 因为 [BukkitMaterial.getId] 不会获取老版物品的 [id]
         */
        val BukkitMaterial.safeId get() = this.getProperty<Int>("id")!!

    }

}