package cn.fd.ratziel.module.item.internal

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import kotlinx.serialization.json.*
import taboolib.common.io.runningResources
import taboolib.module.nms.MinecraftVersion

/**
 * ItemSheet - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2025/4/4 1:04
 */
object ItemSheet {

    val mappings: BiMap<Pair<String, String>, String> by lazy {
        Mapper.initialize("internal/nbt-mappings.json")
    }

    val mappings2: BiMap<String, String> by lazy {
        val path = "internal/nbt-mappings.json"
        // Read from resources
        val bytes = this::class.java.classLoader.getResourceAsStream(path)?.readBytes()
            ?: throw IllegalStateException("File not found: $path!")
        val json = Json.parseToJsonElement(bytes.toString(Charsets.UTF_8))
        // Analyze to map
        HashBiMap.create<String, String>().apply {
            for ((key, verMap) in json.jsonObject) {
                forcePut(key, matchVersion(verMap))
            }
        }
    }

    /** 自定义数据组件名称 **/
    const val CUSTOM_DATA_COMPONENT = "minecraft:custom_data"

    private fun matchVersion(json: JsonElement): String = when (json) {
        is JsonObject ->
            json.toSortedMap(Comparator.comparingInt { it.toInt() }).let { sortedMap ->
                sortedMap.entries.findLast { MinecraftVersion.versionId >= it.key.toInt() }
            }?.let { it.value as? JsonPrimitive }?.content

        is JsonNull -> null
        is JsonPrimitive -> json.content
        else -> null
    } ?: "" // 返回空字符串, 代表不支持

    /**
     * Mapper
     *
     * @author TheFloodDragon
     * @since 2024/4/30 20:25
     */
    private object Mapper {

        /**
         * Initialize from [path]
         */
        fun initialize(path: String): BiMap<Pair<String, String>, String> {
            // Read from resources
            val bytes = runningResources[path] ?: throw IllegalStateException("File not found: $path!")
            val json = Json.parseToJsonElement(bytes.toString(Charsets.UTF_8))
            // Analyze to map
            return HashBiMap.create<Pair<String, String>, String>().apply {
                for ((id, verMap) in json.jsonObject) {
                    val split = id.split('.', limit = 2)
                    forcePut(split[0] to split[1], matchVersion(verMap))
                }
            }
        }

        fun matchVersion(json: JsonElement): String = when (json) {
            is JsonObject ->
                json.toSortedMap(Comparator.comparingInt { it.toInt() }).let { sortedMap ->
                    sortedMap.entries.findLast { MinecraftVersion.versionId >= it.key.toInt() }
                }?.let { it.value as? JsonPrimitive }?.content

            is JsonNull -> null
            is JsonPrimitive -> json.content
            else -> null
        } ?: "" // 返回空字符串, 代表不支持

    }

}