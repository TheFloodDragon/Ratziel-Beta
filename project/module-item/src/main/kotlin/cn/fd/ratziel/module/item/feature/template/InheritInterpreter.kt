package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.function.warning

/**
 * InheritInterpreter - 继承解释器
 *
 * @author TheFloodDragon
 * @since 2025/8/6 22:34
 */
object InheritInterpreter : ItemInterpreter {

    override suspend fun preFlow(stream: ItemStream) {
        stream.tree.withValue { tree ->
            resolveTree(tree)
            resolveTag(tree)
        }
    }

    /**
     * 解析树并合并模板
     */
    @JvmStatic
    fun resolveTree(tree: JsonTree) {
        // 仅处理根节点, 根节点需为对象节点
        val node = tree.root as? JsonTree.ObjectNode ?: return
        // 寻找继承字段
        val field = node.value["inherit"] as? JsonTree.PrimitiveNode ?: return
        val name = field.value.content
        // 处理继承
        val target = findElement(name) ?: return
        // 合并对象
        merge(node, target)
    }

    /**
     * 解析标签
     */
    @JvmStatic
    fun resolveTag(tree: JsonTree) {
        // 直接调用标签解析器 (已知其不需要上下文信息)
        TaggedSectionResolver.resolveSingle(InheritResolver, tree, SimpleContext())
    }

    @JvmStatic
    fun findElement(name: String): JsonObject? {
        val element = TemplateElement.templateMap[name]
        if (element == null) {
            warning("Unknown element named '$name' which is to be inherited!")
            return null
        }
        if (element !is JsonObject) {
            warning("The target to be inherited must be a JsonObject!")
            return null
        }
        return element
    }

    /**
     * 合并目标
     */
    @JvmStatic
    fun merge(source: JsonTree.ObjectNode, target: JsonObject) {
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
            } ?: if (ownValue == null) JsonTree.Companion.parseToNode(targetValue) else continue
        }
        source.value = map // 替换为新 Map
    }

}