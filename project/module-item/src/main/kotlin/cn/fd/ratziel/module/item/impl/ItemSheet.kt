package cn.fd.ratziel.module.item.impl

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import kotlinx.serialization.json.*
import taboolib.common.LifeCycle
import taboolib.common.io.runningResources
import taboolib.common.platform.Awake
import taboolib.module.nms.MinecraftVersion

/**
 * ItemSheet - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2025/4/4 1:04
 */
object ItemSheet {

    lateinit var mappings: BiMap<Pair<String, String>, String>
        private set

    /** 自定义数据组件名称 **/
    const val CUSTOM_DATA_COMPONENT = "custom_data"

    @Awake(LifeCycle.LOAD)
    private fun initialize() {
        mappings = Mapper.initialize("nbt-mappings.json")
    }

    /**
     * Mapper
     *
     * @author TheFloodDragon
     * @since 2024/4/30 20:25
     */
    object Mapper {

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
                    try {
                        val split = id.split('.')
                        put(split[0] to split[1], matchVersion(verMap))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
        } ?: throw IllegalStateException("Unable to match version!")

    }

}