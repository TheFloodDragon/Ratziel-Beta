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
    fun findParent(element: Element) = this.findParent(element, ArrayList())

    @JvmStatic
    private fun findParent(element: Element, records: MutableList<String>): Template? {
        // 添加记录
        records.add(element.name)

        // 看看有没有继承别的模板
        val property = element.property as? JsonObject ?: return null
        val parentName = (property.getBy(*INHERIT_ALIAS) as? JsonPrimitive)?.content ?: return null

        // 尝试寻找父模板 (先从缓存中取)
        var parent: Template? = TemplateElement.templates[parentName]
        // 没有就自己生成
        if (parent == null) {
            // 获取父模板的元素
            val parentElement = TemplateElement.rawTemplates[parentName]
            if (parentElement == null) {
                warning("Unknown element named '$parentName' which is to be inherited for '${element.name}'!")
                return null
            }
            // 元素存在, 先校验
            if (records.contains(parentElement.name)) {
                warning("Circular inheritance detected! Element '${element.name}' wants to inherit the exist template ${parentElement.name}!")
                return null
            } else {
                // 校验通过, 记录下
                records.add(parentElement.name)
                // 后创建父模板 (并递归往上找)
                parent = Template(parentElement, findParent(parentElement, records))
            }
        }
        // 返回最终结果
        return parent
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