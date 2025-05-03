package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.impl.builder.provided.EnhancedListResolver
import kotlinx.serialization.json.JsonElement
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DefaultResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:32
 */
object DefaultResolver : ItemResolver {

    /**
     * 片段解析器列表
     */
    val sectionResolvers: MutableList<ItemSectionResolver> = mutableListOf(
        EnhancedListResolver
    )

    /**
     * 标签解析器列表
     */
    val tagResolvers: MutableList<SectionTagResolver> = CopyOnWriteArrayList()

    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement {
        return this.resolve(JsonTree(element), context)
    }

    fun resolve(tree: JsonTree, context: ArgumentContext): JsonElement {
        JsonTree.unfold(tree.root) {
            for (resolver in sectionResolvers) resolver.resolve(it, context)
        }
        return tree.toElement()
    }

}