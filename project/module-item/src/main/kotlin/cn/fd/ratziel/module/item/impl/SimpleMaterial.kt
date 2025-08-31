package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.api.ItemMaterial
import org.bukkit.Material
import taboolib.library.reflex.ReflexClass
import taboolib.library.xseries.XMaterial

/**
 * SimpleMaterial
 *
 * 由于不可抗力的影响(我不会), 仅支持 [org.bukkit.Material], 即仅支持原版物品
 *
 * @author TheFloodDragon
 * @since 2024/4/5 13:26
 */
@JvmInline
value class SimpleMaterial(private val ref: Material) : ItemMaterial {

    constructor(name: String) : this(findBukkit(name) ?: Material.AIR)

    constructor(id: Int) : this(findBukkit(id) ?: Material.AIR)

    constructor(mat: XMaterial) : this(mat.get()!!)

    constructor(mat: ItemMaterial) : this(mat.name)

    /**
     * 材料标识符 (低版本)
     */
    override val id: Int get() = ref.unsafeId

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
    override fun isEmpty() = this.name == Material.AIR.name || super.isEmpty()

    /**
     * 获取 [Material] 形式
     */
    fun getAsBukkit(): Material = ref

    /**
     * 获取 [XMaterial] 形式
     */
    fun getAsXSeries(): XMaterial = XMaterial.matchXMaterial(ref)

    override fun toString() = "SimpleMaterial(name=$name, id=$id)"

    companion object {

        /**
         * 材料大全
         */
        val materialsMap by lazy {
            HashMap<String, ItemMaterial>().apply {
                Material.entries.forEach { put(it.name, SimpleMaterial(it)) }
            }
        }

        /**
         * 通过名称寻找 [Material] 形式的物品材料
         */
        fun findBukkit(name: String) = Material.getMaterial(name)

        /**
         * 通过标识符寻找 [Material] 形式的物品材料
         */
        fun findBukkit(id: Int) = Material.entries.find { it.unsafeId == id }

        /**
         * 通过标识符寻找 [ItemMaterial] 形式的物品材料
         */
        fun findBy(id: Int) = materialsMap.values.find { it.id == id }

        /**
         * 将 [ItemMaterial] 转换为 [Material] 形式
         */
        fun ItemMaterial.toBukkit(): Material = if (this is SimpleMaterial) this.ref else findBukkit(this.name) ?: Material.AIR

        /**
         * 通过反射获取 [id], 因为 [Material] 不会获取老版物品的 [id]
         */
        val Material.unsafeId get() = bukkitIdField.get(this) as Int

        /**
         * private final int id
         */
        private val bukkitIdField by lazy {
            ReflexClass.Companion.of(Material::class.java, false).structure.getField("id")
        }

    }

}