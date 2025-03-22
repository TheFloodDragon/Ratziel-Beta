@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.SimpleMaterial
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

    companion object : DataProcessor {

        @JvmField
        val PLAYER_HEAD: ItemMaterial = SimpleMaterial(BukkitMaterial.PLAYER_HEAD)

        override fun process(data: ItemData) = data.apply {
            if (tag.isNotEmpty()) material = PLAYER_HEAD
        }

    }

}