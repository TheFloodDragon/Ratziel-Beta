package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import java.util.*

/**
 * ItemTagResolver
 *
 * @author TheFloodDragon
 * @since 2025/5/24 17:36
 */
interface ItemTagResolver {

    val alias: Array<String>

    /**
     * 解析元素
     *
     * @param assignment 解析任务
     * @param context 上下文
     */
    fun resolve(assignment: Assignment, context: ArgumentContext)

    /**
     * 标签解析任务
     */
    class Assignment(
        /**
         * 执行此任务的 [ItemTagResolver]
         */
        val resolver: ItemTagResolver,
        /**
         * 标签参数
         */
        val args: List<String>,
        /**
         * 外部内容 (此标签在哪个字符串节点内部)
         */
        val outside: JsonTree.PrimitiveNode,
    ) {

        /**
         * 标签解析结果 (不赋值代表解析未完成, 不替换内容)
         */
        var result: Optional<String> = Optional.empty()

        /**
         * 完成解析, 设置结果
         */
        fun complete(content: String) {
            this.result = Optional.of(content)
        }

    }

}