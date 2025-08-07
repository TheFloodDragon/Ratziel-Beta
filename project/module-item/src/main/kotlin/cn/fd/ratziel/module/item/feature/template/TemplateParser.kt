package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.getBy
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
    fun findParent(element: Element, records: MutableList<Element> = ArrayList()): Template? {
        // 添加记录
        records.add(element)
        // 解析模板继承的模板
        val property = element.property as? JsonObject ?: return null
        val name = (property.getBy(*INHERIT_ALIAS) as? JsonPrimitive)?.content ?: return null
        var parent: Template?
        parent = TemplateElement.templates[name] // 看看有没有被解析过
        // 没有被解析过, 现场解析下
        if (parent == null) {
            // 获取元素
            val element = TemplateElement.rawTemplates[name]
            if (element == null) {
                warning("Unknown element named '$name' which is to be inherited!")
                return null
            }
            // 尝试继续找父模板
            parent = findParent(element, records)
        }
        // 创建模板
        val template = Template(element, parent)
        // 循环继承检测
        if (parent != null && records.contains(parent.element)) {
            warning("Circular inheritance detected! $template wants to inherit the exist template $parent!")
            return null
        } else return template
    }

    /**
     * 根据名称寻找模板, 无法找到时警告
     */
    @JvmStatic
    fun findTemplate(name: String): Template? {
        val template = TemplateElement.templates[name]
        if (template == null) {
            warning("Unknown element named '$name' which is to be inherited!")
            return null
        } else return template
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