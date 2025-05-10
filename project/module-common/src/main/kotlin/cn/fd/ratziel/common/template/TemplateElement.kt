package cn.fd.ratziel.common.template

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.serialization.json.JsonTree
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.function.warning
import java.util.concurrent.ConcurrentHashMap

/**
 * TemplateElement
 *
 * @author TheFloodDragon
 * @since 2025/5/10 16:47
 */
@NewElement("template")
object TemplateElement : ElementHandler {

    /**
     * 模板表
     */
    val templateMap: MutableMap<String, JsonElement> = ConcurrentHashMap()

    override fun handle(element: Element) {
        // 解析模板继承
        val resolved = resolveInherit(element.property)
        // 加入到模板表内
        templateMap[element.name] = resolved
    }

    /**
     * 解析模板继承
     */
    fun resolveInherit(element: JsonElement): JsonElement {
        val tree = JsonTree(element)
        resolveInherit(tree.root)
        return tree.toElement()
    }

    /**
     * 解析模板继承
     */
    fun resolveInherit(node: JsonTree.Node) {
        // 仅处理根节点, 根节点需为对象节点
        if (node.parent != null || node !is JsonTree.ObjectNode) return
        // 寻找继承字段
        val field = node.value["inherit"] as? JsonTree.PrimitiveNode ?: return
        node.value = node.value.filter { it.key != "inherit" } // 删除继承节点
        val name = field.value.content
        // 处理继承
        val target = findObjectElement(name) ?: return
        // 合并对象
        merge(node, target)
    }

    fun findObjectElement(name: String): JsonObject? {
        val element = templateMap[name]
        if (element == null) {
            warning("Unknown element named '$name' which is to be inherited!")
            return null
        }
        val property = element
        if (property !is JsonObject) {
            warning("The target to be inherited must be a JsonObject!")
            return null
        }
        return property
    }

    /**
     * 合并目标
     */
    private fun merge(source: JsonTree.ObjectNode, target: JsonObject) {
        val map = source.value.toMutableMap()
        for ((key, targetValue) in target) {
            // 获取自身的数据
            val ownValue = map[key]
            // 如果自身数据不存在, 或者允许替换, 则直接替换, 反则跳出循环
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