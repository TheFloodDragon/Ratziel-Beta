package cn.fd.ratziel.item.meta.serializers.vmeta

import cn.fd.ratziel.item.meta.VItemDurability
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.serializer

/**
 * ItemDurabilitySerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/15 9:03
 */
object ItemDurabilitySerializer : JsonTransformingSerializer<VItemDurability>(serializer()) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        println(element)
        return super.transformDeserialize(element)
    }

}