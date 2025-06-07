@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.internal.serializers.EnchantmentSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.library.xseries.XEnchantment

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
)