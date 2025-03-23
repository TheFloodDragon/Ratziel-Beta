@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.util.SkullData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.library.xseries.XMaterial

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
    @JsonNames("skull")
    var head: SkullData? = null
) {

    companion object : DataProcessor {

        val PLAYER_HEAD: ItemMaterial = SimpleMaterial(XMaterial.PLAYER_HEAD)

        override fun process(data: ItemData) = data.apply {
            if (tag.isNotEmpty()) {
                material = PLAYER_HEAD
                tag = tag["head"]!! as NbtCompound // 释放
            }
        }

    }

}