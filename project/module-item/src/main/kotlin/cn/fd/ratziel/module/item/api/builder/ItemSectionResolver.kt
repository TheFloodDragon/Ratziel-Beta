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
     * 解析处理 [JsonTree.Node]
     *
     * @param node 要解析处理节点
     * @param context 上下文
     */
    fun resolve(node: JsonTree.Node, context: ArgumentContext) = Unit

    /**
     * 解析处理字符串
     *
     * @param section 要解析处理的部分 (字符串)
     * @param context 上下文
     */
    fun resolve(section: String, context: ArgumentContext): String = section

    /**
     * 接受要处理的字符串时调用
     */
    fun accept(section: String) = Unit

}