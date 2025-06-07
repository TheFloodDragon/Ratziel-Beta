@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.internal.serializers.EnchantmentSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.library.xseries.XEnchantment
import taboolib.module.nms.MinecraftVersion

typealias EnchantmentType = @Serializable(EnchantmentSerializer::class) XEnchantment

/**
 * ItemEnchant
 *
 * @author TheFloodDragon
 * @since 2025/6/7 09:19
 */
@Serializable
class ItemEnchant(
    /**
     * 附魔
     */
    @JsonNames("enchant", "enchants", "enchantment")
    var enchantments: MutableMap<EnchantmentType, Int>? = null,
    /**
     * 是否显示附魔光效 (1.20.5+)
     */
    @JsonNames("glint")
    var glintOverride: Boolean? = null,
    /**
     * 物品的附魔能力 (1.20.5+)
     * 当附魔能力为 0 时, 物品无法被附魔
     */
    var enchantable: Int? = null,
) {

    init {
        // 检查附魔表
        enchantments?.forEach { check(it.value) }
    }

    /**
     * 获取魔咒等级
     *
     * @return 魔咒不存在时返回 0
     */
    fun getLevel(enchantment: EnchantmentType): Int = this.enchantments?.get(enchantment) ?: 0

    /**
     * 设置魔咒
     */
    fun setEnchantment(enchantment: EnchantmentType, level: Int) {
        // 检查等级
        check(level)
        // 魔咒
        val enchantments = this.enchantments
        if (enchantments == null) {
            this.enchantments = HashMap(mapOf(enchantment to level))
        } else enchantments[enchantment] = level
    }

    /**
     * 删除魔咒
     */
    fun remove(enchantmentType: EnchantmentType) = this.enchantments?.remove(enchantmentType)

    /**
     * 附魔等级检查
     */
    private fun check(level: Int) {
        if (MinecraftVersion.versionId >= 12005) {
            require(level in 0..255) { "Enchantment's level must be between 0 and 255" }
        }
    }

}