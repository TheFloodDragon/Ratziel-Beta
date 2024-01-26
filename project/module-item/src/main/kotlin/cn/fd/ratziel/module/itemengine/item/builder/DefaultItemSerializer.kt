package cn.fd.ratziel.module.itemengine.item.builder

import cn.fd.ratziel.core.function.futureAsync
import cn.fd.ratziel.core.serialization.edit
import cn.fd.ratziel.core.serialization.get
import cn.fd.ratziel.module.itemengine.api.builder.ItemKSerializer
import cn.fd.ratziel.module.itemengine.api.builder.ItemSerializer
import cn.fd.ratziel.module.itemengine.item.meta.VItemCharacteristic
import cn.fd.ratziel.module.itemengine.item.meta.VItemDisplay
import cn.fd.ratziel.module.itemengine.item.meta.VItemDurability
import cn.fd.ratziel.module.itemengine.item.meta.VItemMeta
import cn.fd.ratziel.module.itemengine.nbt.NBTMapper
import cn.fd.ratziel.module.itemengine.nbt.NBTTag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemSerializer
 *
 * @author TheFloodDragon
 * @since 2024/1/26 18:05
 */
open class DefaultItemSerializer(
    val defaultJson: Json,
) : ItemKSerializer<VItemMeta> {

    /**
     * 通过Json反序列化
     */
    fun deserializeByJson(json: Json, element: JsonElement): VItemMeta {
        // 结构化解析
        if (checkIsStructured(element)) return json.decodeFromJsonElement(VItemMeta.serializer(), element)
        // 一般解析
        val display = futureAsync { json.decodeFromJsonElement(VItemDisplay.serializer(), element) }
        val characteristic = futureAsync { json.decodeFromJsonElement(VItemCharacteristic.serializer(), element) }
        val durability = futureAsync { json.decodeFromJsonElement(VItemDurability.serializer(), element) }
        val nbt: CompletableFuture<NBTTag> = futureAsync { NBTSerializer.deserializeFromJson(element) }
        return VItemMeta(display.get(), characteristic.get(), durability.get(), nbt.get())
    }

    /**
     * 通过Json序列化
     */
    fun serializeByJson(json: Json, value: VItemMeta) =
        json.encodeToJsonElement(VItemMeta.serializer(), value).let {
            // 开启结构化解析
            it.jsonObject.edit { put(NODE_STRUCTURED, JsonPrimitive(true)) }
        }

    companion object {

        /**
         * 使用到的序列化器
         * 包括 [KSerializer] 和 [ItemSerializer]
         */
        val serializers = arrayOf(
            VItemDisplay.serializer(),
            VItemCharacteristic.serializer(),
            VItemDurability.serializer(),
            VItemMeta.serializer(),
            NBTSerializer
        )

        val usedNodes = serializers.flatMap {
            ItemSerializer.getUsedNodes(it)
        }.toSet().toTypedArray()

    }

    /**
     * NBTSerializer - [NBTTag] 的物品序列化器
     * 注意: 不要和 [NBTMapper] 搞混, 这个只是套了 [NBTMapper] 的方法, 方便用的
     */
    internal object NBTSerializer : ItemSerializer<NBTTag> {

        override val usedNodes = arrayOf("nbt", "itemTag", "itemTags")

        override fun serializeToJson(value: NBTTag): JsonElement = Json.decodeFromString(value.toString())

        fun deserialize(element: JsonElement, source: NBTTag): NBTTag? =
            (element.takeIf { element is JsonObject } as? JsonObject)?.let { o ->
                o[usedNodes]?.let { NBTMapper.mapFromJson(it, source) }
            }

        override fun deserializeFromJson(element: JsonElement) = deserialize(element, NBTTag()) ?: NBTTag()

    }

    override val descriptor = VItemMeta.serializer().descriptor

    override var usedNodes = Companion.usedNodes

    override fun serializeToJson(value: VItemMeta) = serializeByJson(defaultJson, value)

    override fun deserializeFromJson(element: JsonElement) = deserializeByJson(defaultJson, element)

    /**
     * 判断 "是否结构化解析"
     */
    val NODE_STRUCTURED = "isStructured"
    fun checkIsStructured(element: JsonElement): Boolean =
        kotlin.runCatching { element.jsonObject[NODE_STRUCTURED]!!.jsonPrimitive.boolean }.getOrElse { false }

}