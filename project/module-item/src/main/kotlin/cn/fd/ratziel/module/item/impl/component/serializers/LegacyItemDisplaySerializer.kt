@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component.serializers

import cn.altawk.nbt.NbtTransformingSerializer
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer

/**
 * LegacyItemDisplaySerializer - 1.20.5以下的低版本支持
 *
 * @author TheFloodDragon
 * @since 2025/4/19 15:11
 */
class LegacyItemDisplaySerializer(
    serializer: KSerializer<ItemDisplay>
) : NbtTransformingSerializer<ItemDisplay>(serializer, true) {

    override fun transformDeserialize(tag: NbtTag): NbtTag {
        if (tag is NbtCompound) {
            val unfolded = tag["display"]
            if (unfolded != null) return unfolded
        }
        return super.transformSerialize(tag)
    }

    override fun transformSerialize(tag: NbtTag): NbtTag {
        return NbtCompound { put("display", tag) }
    }

}