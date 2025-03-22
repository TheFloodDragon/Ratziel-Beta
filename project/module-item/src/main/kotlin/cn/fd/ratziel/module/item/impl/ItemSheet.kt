package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.module.item.impl.ItemSheet.Mapper.mappings
import kotlinx.serialization.json.*
import taboolib.common.LifeCycle
import taboolib.common.io.runningResources
import taboolib.common.platform.Awake
import taboolib.module.nms.MinecraftVersion

/**
 * ItemSheet - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
object ItemSheet {

    var path: String = "nbt-mappings.json"

    val mappings: Map<Pair<String, String>, String> get() = Mapper.mappings

    val mappingsReversed: Map<String, Pair<String, String>> get() = Mapper.mappingsReversed

    @Awake(LifeCycle.LOAD)
    fun init() {
        Mapper.initialize(path)
    }

    /**
     * Mapper
     *
     * @author TheFloodDragon
     * @since 2024/4/30 20:25
     */
    private object Mapper {

        lateinit var mappings: Map<Pair<String, String>, String>
            private set

        lateinit var mappingsReversed: Map<String, Pair<String, String>>
            private set

        /**
         * Initialize [mappings] from [path]
         */
        fun initialize(path: String) {
            // Read from resources
            val bytes = runningResources[path] ?: throw IllegalStateException("File not found: $path!")
            val json = Json.Default.parseToJsonElement(bytes.toString(Charsets.UTF_8))
            // Analyze to map
            mappings = buildMap {
                for ((id, verMap) in json.jsonObject) {
                    try {
                        val split = id.split('.')
                        put(split[0] to split[1], matchVersion(verMap))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            // 对调 Key 和 Value 形成 mappingsReversed
            mappingsReversed = buildMap { mappings.forEach { put(it.value, it.key) } }
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