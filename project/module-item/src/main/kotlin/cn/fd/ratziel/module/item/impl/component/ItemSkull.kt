@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtEncoder
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.util.SkullData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNames

/**
 * ItemSkull
 *
 * @author TheFloodDragon
 * @since 2024/10/1 13:48
 */
@Serializable(ItemSkull.Companion::class)
data class ItemSkull(
    /**
     * 头颅材质数据
     */
    @JsonNames("skull", "head", "skullData")
    var skullTexture: SkullData? = null
) {

    @Serializer(ItemSkull::class)
    companion object : DataProcessor, KSerializer<ItemSkull> {

        @JvmField
        val PLAYER_HEAD: ItemMaterial = SimpleMaterial(BukkitMaterial.PLAYER_HEAD)

        override fun process(data: ItemData) = data.apply {
            if (tag.isNotEmpty()) material = PLAYER_HEAD
        }

        override fun serialize(encoder: Encoder, value: ItemSkull) {
            val data = value.skullTexture
            if (encoder is NbtEncoder && data != null) {
                encoder.encodeNbtTag(data.tag)
            } else super.serialize(encoder, value)
        }

    }

}