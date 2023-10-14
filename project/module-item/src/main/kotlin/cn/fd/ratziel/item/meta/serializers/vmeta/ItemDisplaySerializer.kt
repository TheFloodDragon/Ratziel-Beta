@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.item.meta.serializers.vmeta

import cn.fd.ratziel.item.meta.VItemDisplay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * ItemDisplaySerializer
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:59
 */
object ItemDisplaySerializer :JsonTransformingSerializer<VItemDisplay>(VItemDisplay.serializer()){

    override fun transformSerialize(element: JsonElement): JsonElement {
        println(element.toString())
        return element
    }

    override fun transformDeserialize(element: JsonElement): JsonElement {
        println(element.toString())
        return element
    }

}