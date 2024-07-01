package cn.fd.ratziel.module.item.nms

import kotlinx.serialization.json.*
import taboolib.common.io.runningResources
import taboolib.module.nms.MinecraftVersion

/**
 * ItemSheet - 物品NBT标签映射表
 *
 * @author TheFloodDragon
 * @since 2023/11/4 11:41
 */
object ItemSheet {

    // Universal
    val CUSTOM_DATA by mapping("CUSTOM_DATA")
    val DISPLAY_NAME by mapping("DISPLAY_NAME")
    val DISPLAY_LORE by mapping("DISPLAY_LORE")
    val ENCHANTMENTS by mapping("ENCHANTMENTS")
    val ATTRIBUTE_MODIFIERS by mapping("ATTRIBUTE_MODIFIERS")
    val CUSTOM_MODEL_DATA by mapping("CUSTOM_MODEL_DATA")
    val REPAIR_COST by mapping("REPAIR_COST")
    val DAMAGE by mapping("DAMAGE")
    val UNBREAKABLE by mapping("UNBREAKABLE")
    val DISPLAY_LOCAL_NAME by mapping("DISPLAY_LOCAL_NAME") // Called "ITEM_NAME" on 1.20.5+
    val DYED_COLOR by mapping("DYED_COLOR")
    val POTION_COLOR by mapping("POTION_COLOR")

    // Custom Features And 1.20.5+
    val MAX_DAMAGE by mapping("MAX_DAMAGE")
    val MAX_STACK_SIZE by mapping("MAX_STACK_SIZE")

    // Only 1.20.5+
    val FOOD by mapping("FOOD")
    val RARITY by mapping("RARITY")
    val FIRE_RESISTANT by mapping("FIRE_RESISTANT")
    val ENCHANTMENT_GLINT_OVERRIDE by mapping("ENCHANTMENT_GLINT_OVERRIDE")
    val HIDE_TOOLTIP by mapping("HIDE_TOOLTIP")

    // 1.20.5- But be retained by CraftBukkit
    val HIDE_FLAG by mapping("HIDE_FLAG")

    fun mapping(name: String): Lazy<String> = lazy { Mapper.map(name) }

    /**
     * Mapper
     *
     * @author TheFloodDragon
     * @since 2024/4/30 20:25
     */
    object Mapper {

        var path = "nbt-mappings.json"

        var mappingData: JsonElement = initData()

        /**
         * 从[mappingData]中获取对应映射值
         * 注意: 所有操作均为危险操作 (不考虑数据缺失)
         */
        fun map(name: String): String {
            val data = mappingData.jsonObject[name]!!.jsonObject
            // 使用固定值
            return matchVersion(data)
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

}