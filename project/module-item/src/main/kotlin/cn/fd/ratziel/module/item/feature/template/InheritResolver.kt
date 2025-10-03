package cn.fd.ratziel.module.item.feature.template

import cn.altawk.nbt.NbtPath
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
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
            // Name.Path 形式, 解析自己的
            1 -> {
                val self = context.pop(Element::class.java)
                path = NbtPath(args[0])
                val find = read(self.property, path)
                if (find == null) {
                    warning("Cannot find element by path '$path' from '${self.name}'.")
                    return null
                } else return platten(find)
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
        val template = TemplateElement.findBy(name) ?: return null
        val find = read(template.element, path)
        if (find == null) {
            warning("Cannot find element by path '$path' from '$name'.")
            return null
        }
        return platten(find)
    }

    @JvmStatic
    private fun platten(find: JsonElement): String? {
        if (find is JsonPrimitive) {
            return find.content
        } else if (find is JsonArray) {
            return find.joinToString(EnhancedListResolver.NEWLINE) { it.toString() }
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