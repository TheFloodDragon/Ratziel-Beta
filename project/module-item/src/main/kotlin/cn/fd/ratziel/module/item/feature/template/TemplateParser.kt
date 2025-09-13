package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.getBy
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.platform.function.warning

/**
 * TemplateParser
 *
 * @author TheFloodDragon
 * @since 2025/8/7 12:39
 */
object TemplateParser {

    @JvmField
    val INHERIT_ALIAS = arrayOf("inherit", "extend")

    @JvmStatic
    fun parse(element: Element): Template {
        return Template(element, findParents(element.property))
    }

    @JvmStatic
    fun findParents(element: JsonElement): Set<String> {
        val property = element as? JsonObject ?: return emptySet()
        return when (val section = property.getBy(*INHERIT_ALIAS)) {
            is JsonPrimitive -> setOf(section.content)
            is JsonArray -> section.mapNotNull { (it as? JsonPrimitive)?.content }.toSet()
            else -> emptySet()
        }
    }

    @JvmStatic
    fun findParents(element: JsonTree.ObjectNode): Set<String> {
        return when (val section = element.value.getBy(*INHERIT_ALIAS)) {
            is JsonTree.PrimitiveNode -> setOf(section.value.content)
            is JsonTree.ArrayNode -> section.value.mapNotNull { (it as? JsonTree.PrimitiveNode)?.value?.content }.toSet()
            else -> emptySet()
        }
    }

    /**
     * 根据模板寻找其元素属性 [JsonObject] 类型, 无法找到时警告
     */
    @JvmStatic
    fun findElement(template: Template): JsonObject? {
        val element = template.element.property
        if (element !is JsonObject) {
            warning("The target to be inherited must be a JsonObject!")
            return null
        } else return element
    }

    /**
     * 合并目标
     */
    @JvmStatic
    fun merge(source: JsonTree.ObjectNode, target: Map<String, JsonElement>) {
        val map = source.value.toMutableMap()
        for ((key, targetValue) in target) {
            // 获取自身的数据
            val ownValue = map[key]
            // 如果自身数据不存在, 则直接替换, 反则跳出循环
            map[key] = when (targetValue) {
                // 目标值为 Compound 类型
                is JsonObject -> (ownValue as? JsonTree.ObjectNode)
                    ?.also { merge(it, targetValue) } // 同类型合并
                // 目标值为基础类型
                else -> null
            } ?: if (ownValue == null) JsonTree.parseToNode(targetValue) else continue
        }
        source.value = map // 替换为新 Map
    }

}