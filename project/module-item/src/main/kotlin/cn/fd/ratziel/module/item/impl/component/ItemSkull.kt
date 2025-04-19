@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.NbtTransformingSerializer
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.util.SkullData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import taboolib.library.xseries.XMaterial

/**
 * ItemSkull
 *
 * @author TheFloodDragon
 * @since 2024/10/1 13:48
 */
@Serializable(ItemSkull.Serializer::class)
@KeepGeneratedSerializer
data class ItemSkull(
    /**
     * 头颅材质数据
     */
    @JsonNames("skull")
    var head: SkullData? = null
) {

    companion object : DataProcessor {

        override fun process(data: ItemData) = data.apply {
            if (tag.isNotEmpty()) {
                material = SimpleMaterial(XMaterial.PLAYER_HEAD) // 设置材质
            }
        }

    }

    internal object Serializer : NbtTransformingSerializer<ItemSkull>(generatedSerializer(), true) {

        override fun transformSerialize(tag: NbtTag): NbtTag {
            if (tag is NbtCompound) {
                val unfolded = tag[ItemSkull::head.name]
                if (unfolded != null) return unfolded
            }
            return super.transformDeserialize(tag)
        }

    }

}