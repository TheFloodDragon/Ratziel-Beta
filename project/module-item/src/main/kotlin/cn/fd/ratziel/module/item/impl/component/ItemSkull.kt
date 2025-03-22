@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.util.SkullData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * ItemSkull
 *
 * @author TheFloodDragon
 * @since 2024/10/1 13:48
 */
@Serializable
data class ItemSkull(
    /**
     * 头颅材质数据
     */
    @JsonNames("skull", "head", "skullData")
    var skullTexture: SkullData? = null
) {

//    companion object : ItemTransformer<ItemSkull> {
//
//        @JvmField
//        val PLAYER_HEAD: ItemMaterial = SimpleMaterial(BukkitMaterial.PLAYER_HEAD)
//
//        override fun transform(data: ItemData, component: ItemSkull) {
//            // 当源数据的材料不为空并且是头颅时
//            if (!data.material.isEmpty() && data.material != PLAYER_HEAD) return
//            // 转换头颅数据
//            val skullData = component.skullTexture ?: return
//            // 重新设置材料
//            data.material = PLAYER_HEAD
//            // 应用数据标签
//            data.tag.merge(skullData.tag, true)
//        }
//
//        override fun detransform(data: ItemData): ItemSkull {
//            val meta = RefItemMeta.new(RefItemMeta.META_SKULL, data.tag)
//            return ItemSkull(SkullUtil.fetchSkullData(meta))
//        }
//
//    }

}