package cn.fd.ratziel.module.item.impl.builder.provided

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.common.template.TemplateElement
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
        return TemplateElement.resolveInherit(node)
    }

    override fun resolve(element: List<String>, context: ArgumentContext): String? {
        throw UnsupportedOperationException("InheritResolver requires Node.")
    }

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
        val target = TemplateElement.findObjectElement(name) ?: return null
        val find = read(target, path)
        if (find == null) {
            warning("Cannot find element by path '$path'.")
            return null
        }
        // 开始插入合并
        if (find is JsonPrimitive) {
            return find.content
        } else if (find is JsonArray) {
            if (node.parent !is JsonTree.ArrayNode) {
                warning("Cannot inherit a JsonArray to a non JsonArray.")
                return null
            }
            // 仅支持元素全是 JsonPrimitive 的 JsonArray
            else if (find.all { it is JsonPrimitive }) {
                return find.joinToString(EnhancedListResolver.NEWLINE) { (it as JsonPrimitive).content }
            } else {
                warning("Inline inheritance in a array does not support complex JsonArray.")
                return null
            }
        }
        return null
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

}