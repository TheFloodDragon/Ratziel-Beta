package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree

/**
 * ItemSectionResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:16
 */
interface ItemSectionResolver {

    /**
     * 解析处理 [JsonTree.Node] 节点
     *
     * @param node 要解析处理的节点
     * @param context 上下文
     */
    fun resolve(node: JsonTree.Node, context: ArgumentContext)

}