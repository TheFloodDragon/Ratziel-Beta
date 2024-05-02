package cn.fd.ratziel.module.item.reflex

import kotlinx.serialization.json.*
import taboolib.common.io.runningResources
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.obcClass

/**
 * ItemMapper
 *
 * @author TheFloodDragon
 * @since 2024/4/30 20:25
 */
object ItemMapper {

    var path = "nbt-mappings.json"

    var mappingData: JsonElement = initData()

    /**
     * 从[mappingData]中获取对应映射值
     * 注意: 所有操作均为危险操作 (不考虑数据缺失)
     */
    fun map(name: String): String {
        val data = mappingData.jsonObject[name]!!.jsonObject
        // 使用静态字段反射
        val field = runCatching { data["field"]?.let { mapFiled(it).nmsName } }.getOrNull()
        if (field != null) return field
        // 备用 Fallback
        val fallback = data["hold"]?.let { matchVersion(it) }
        return fallback ?: throw IllegalStateException("Failed on mapping: $name")
    }

    fun mapFiled(json: JsonElement): RefItemMeta.RefItemMetaKey {
        val split = matchVersion(json).split("#")
        return RefItemMeta.RefItemMetaKey(split[1], obcClass(split[0]))
    }

    fun matchVersion(json: JsonElement): String = when (json) {
        is JsonObject ->
            json.toSortedMap(Comparator.comparingInt { it.toInt() }).let { sortedMap ->
                sortedMap.entries.findLast { MinecraftVersion.majorLegacy >= it.key.toInt() }
            }?.let { it.value as? JsonPrimitive }?.content

        is JsonNull -> null
        is JsonPrimitive -> json.content
        else -> null
    } ?: throw IllegalStateException("Unable to match version!")

    fun initData(): JsonElement {
        val bytes = runningResources[path] ?: throw IllegalStateException("Not found: $path")
        return Json.parseToJsonElement(bytes.toString(Charsets.UTF_8))
    }

}