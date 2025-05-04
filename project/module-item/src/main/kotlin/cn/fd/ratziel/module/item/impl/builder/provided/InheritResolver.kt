package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.common.element.ElementEvaluator
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.impl.builder.SectionTagResolver
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.platform.Awake
import taboolib.common.platform.function.warning

/**
 * InheritResolver - 继承解析器
 *
 * @author TheFloodDragon
 * @since 2025/5/4 15:44
 */
@Awake
object InheritResolver : ItemSectionResolver, SectionTagResolver("extend", "inherit") {

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        // 仅处理根节点, 根节点需为对象节点
        if (node.parent != null || node !is JsonTree.ObjectNode) return
        // 寻找继承字段
        val field = node.value["inherit"] as? JsonTree.PrimitiveNode ?: return
        val name = field.value.content
        // 处理继承
        val target = findElement(name) ?: return
        // 合并对象
        merge(node, target)
    }

    override fun resolve(element: List<String>, context: ArgumentContext) = throw UnsupportedOperationException("InheritResolver requires Node.")

    override fun resolve(element: List<String>, node: JsonTree.PrimitiveNode, context: ArgumentContext): String? {
        // 元素名称
        var name: String
        /*
          路径
          为什么用 NbtPath 呢?
          因为底层逻辑都是一样的, 重写一个岂不麻烦?
          不用白不用.
         */
        var path: NbtPath

        // 看看传过来什么东西
        when (element.size) {
            0 -> return null // 没元素名, 没路径, 解析什么?
            // Name.Path 形式, 可以的
            1 -> {
                val np = NbtPath(element[0])
                name = (np.first() as NbtPath.NameNode).name
                path = NbtPath(np.drop(1))
            }
            // Name:Path 形式
            2 -> {
                name = element[0]
                path = NbtPath(element[1])
            }
            // >=3, 也就是 Name:Path 形式, 同时 Path形式: hello:[1]:world
            else -> {
                name = element[0]
                path = NbtPath(element.drop(1).flatMap { NbtPath(it) })
            }
        }

        // 根据路径寻找
        val target = findElement(name) ?: return null
        val find = read(target, path)
        // 开始插入合并
        val parent = node.parent ?: return null // 这能为根节点?
        // 父节点为数组节点
        if (parent is JsonTree.ArrayNode) {
            when (find) {
                is JsonPrimitive -> find.content
                is JsonArray -> {
                    // 仅支持元素全是 JsonPrimitive 的 JsonArray
                    if (find.all { it is JsonPrimitive }) {
                        find.joinToString(EnhancedListResolver.NEWLINE) { (it as JsonPrimitive).content }
                    } else {
                        warning("Inline inheritance in a array does not support complex JsonArray.")
                        return null
                    }
                }

                else -> null
            }
        } else if (parent is JsonTree.PrimitiveNode) {
            if (find is JsonPrimitive) {
                return find.content
            } else {
                warning("Inline inheritance does not support non JsonPrimitive.")
                return null
            }
        }
        return null
    }

    private fun findElement(name: String): JsonObject? {
        val element = ElementEvaluator.loadedElements[name]
        if (element == null) {
            warning("Unknown element named '$name' which is to be inherited!")
            return null
        }
        val property = element.property
        if (property !is JsonObject) {
            warning("The target to be inherited must be a JsonObject!")
            return null
        }
        return property
    }

    private fun read(element: JsonElement, path: NbtPath): JsonElement? {
        var result = element
        for (node in path) {
            result = when (node) {
                is NbtPath.NameNode -> (result as? JsonObject)?.get(node.name) ?: return null
                is NbtPath.IndexNode -> (result as? JsonArray)?.get(node.index) ?: return null
            }
        }
        return result
    }

    /**
     * 合并目标
     */
    fun merge(source: JsonTree.ObjectNode, target: JsonObject) {
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