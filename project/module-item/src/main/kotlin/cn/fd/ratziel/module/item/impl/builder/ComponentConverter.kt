package cn.fd.ratziel.module.item.impl.builder

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import kotlinx.serialization.json.JsonElement
import taboolib.common.platform.function.severe

/**
 * ComponentConverter
 *
 * @author TheFloodDragon
 * @since 2025/7/6 15:03
 */
object ComponentConverter {

    /**
     * 序列化组件
     */
    @JvmStatic
    fun transformToNbtTag(integrated: ItemRegistry.ComponentIntegrated<*>, element: JsonElement): Result<NbtTag> {
        // 第一步: 解码成物品组件
        val component = try {
            // 解码
            @Suppress("UNCHECKED_CAST")
            deserializeFromJsonElement(integrated as ItemRegistry.ComponentIntegrated<Any>, element)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by '${integrated.serializer}'!")
            ex.printStackTrace()
            return Result.failure(ex)
        }
        // 第二步: 编码成组件数据
        try {
            // 编码
            return Result.success(serializeToNbtTag(integrated, component))
        } catch (ex: Exception) {
            severe("Failed to transform component by '${integrated.serializer}'! Source component: $component")
            ex.printStackTrace()
            return Result.failure(ex)
        }
    }

    /**
     * [JsonElement]数据 转化为 组件
     */
    @JvmStatic
    fun <T> serializeToJsonElement(integrated: ItemRegistry.ComponentIntegrated<T>, component: T): JsonElement {
        return ItemElement.json.encodeToJsonElement(integrated.serializer, component)
    }

    /**
     * [JsonElement]数据 转化为 组件
     */
    @JvmStatic
    fun <T> deserializeFromJsonElement(integrated: ItemRegistry.ComponentIntegrated<T>, element: JsonElement): T {
        return ItemElement.json.decodeFromJsonElement(integrated.serializer, element)
    }

    /**
     * 组件 转化为 [NbtTag]数据
     */
    @JvmStatic
    fun <T> serializeToNbtTag(integrated: ItemRegistry.ComponentIntegrated<T>, component: T): NbtTag {
        return ItemElement.nbt.encodeToNbtTag(integrated.serializer, component)
    }

    /**
     * [NbtTag]数据 转化为 组件
     */
    @JvmStatic
    fun <T> deserializeFromNbtTag(integrated: ItemRegistry.ComponentIntegrated<T>, tag: NbtTag): T {
        return ItemElement.nbt.decodeFromNbtTag(integrated.serializer, tag)
    }

}