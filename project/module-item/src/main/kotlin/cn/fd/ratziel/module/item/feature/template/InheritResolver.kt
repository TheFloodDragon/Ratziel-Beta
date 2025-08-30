package cn.fd.ratziel.module.item.feature.template

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.platform.function.warning

/**
 * InheritResolver - 继承解析器 (受 [InheritInterpreter] 管控)
 *
 * @author TheFloodDragon
 * @since 2025/5/4 15:44
 */
object InheritResolver : ItemTagResolver {

    override val alias get() = TemplateParser.INHERIT_ALIAS

    override fun resolve(args: List<String>, context: ArgumentContext): String? {
        // 元素名称
        val name: String
        /*
          路径
          为什么用 NbtPath 呢?
          因为底层逻辑都是一样的, 重写一个岂不麻烦?
          不用白不用.
         */
        val path: NbtPath

        // 看看传过来什么东西
        when (args.size) {
            0 -> return null // 没元素名, 没路径, 解析什么?
            // Name.Path 形式, 可以的
            1 -> {
                val np = NbtPath(args[0])
                name = (np.first() as NbtPath.NameNode).name
                path = NbtPath(np.drop(1))
            }
            // Name:Path 形式
            2 -> {
                name = args[0]
                path = NbtPath(args[1])
            }
            // >=3, 也就是 Name:Path 形式, 同时 Path形式: hello:[1]:world
            else -> {
                name = args[0]
                path = NbtPath(args.drop(1).flatMap { NbtPath(it) })
            }
        }

        // 根据路径寻找
        val template = TemplateParser.findTemplate(name) ?: return null
        var find: JsonElement? = null
        // 链式查找 (从底部开始)
        for (t in template.asChain()) {
            val element = t.element.property as? JsonObject ?: continue
            find = read(element, path) ?: continue
        }
        if (find == null) {
            warning("Cannot find element by path '$path'.")
            return null
        }
        // 开始插入合并
        if (find is JsonPrimitive) {
            return find.content
        } else if (find is JsonArray) {
            // 仅支持元素全是 JsonPrimitive 的 JsonArray
            if (find.all { it is JsonPrimitive }) {
                return find.joinToString(EnhancedListResolver.NEWLINE) { (it as JsonPrimitive).content }
            } else {
                warning("Inline inheritance in a array does not support complex JsonArray.")
            }
        }
        return null
    }

    @JvmStatic
    fun read(element: JsonElement, path: NbtPath): JsonElement? {
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