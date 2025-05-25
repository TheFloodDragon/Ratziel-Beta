package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.exception.ArgumentNotFoundException
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.splitNonEscaped
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.common.util.VariableReader
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

/**
 * TaggedSectionResolver
 *
 * 解析带有标签的字符串
 *
 * @author TheFloodDragon
 * @since 2025/5/3 19:34
 */
object TaggedSectionResolver {

    /**
     * 标签解析器列表
     */
    val tagResolvers: MutableMap<String, ItemTagResolver> = ConcurrentHashMap()

    /**
     * 分析根节点
     * @return 返回内部分析后的数据
     */
    fun analyze(root: JsonTree.Node): AnalyzedData {
        val analyzed = HashMap<JsonTree.PrimitiveNode, List<UnresolvedPart>>()

        // 展开树, 解析
        JsonTree.unfold(root) { node ->
            // 仅解析字符串, 其他类型哪有可能有标签
            if (node is JsonTree.PrimitiveNode
                && node.value.isString
                && node.value !is JsonNull
            ) {
                analyzed[node] = analyzeTaggedNode(node)
            }
        }

        return AnalyzedData(analyzed)
    }

    private fun analyzeTaggedNode(node: JsonTree.PrimitiveNode): List<UnresolvedPart> {
        val analyzedParts = ArrayList<UnresolvedPart>()
        // 使用读取器读取标签
        val parts = reader.readToFlatten(node.value.content)
        for (part in parts) {
            // 如果是标签片段, 则分析标签解析器参数, 创建解析任务
            if (part.isVariable) {
                val split = part.text.splitNonEscaped(TAG_ARG_SEPARATION)
                val name = split.firstOrNull() ?: continue // 解析器名称
                // 获取解析器
                val resolver = tagResolvers[name] ?: continue
                // 创建解析任务
                val task = ItemTagResolver.Assignment(resolver, split.drop(1), node)
                // 添加部分
                analyzedParts.add(UnresolvedPart(reader.start + part.text + reader.end, task))
            } else {
                analyzedParts.add(UnresolvedPart(part.text, null))
            }
        }
        return analyzedParts
    }

    /**
     * 执行解析 [ItemTagResolver.Assignment], 返回处理后的字符串
     * @return 若标签不合法或者处理后结果为空, 则返回空
     */
    fun resolveTag(assignment: ItemTagResolver.Assignment, context: ArgumentContext): String? {
        val resolver = assignment.resolver
        try {
            // 解析标签
            resolver.resolve(assignment, context)
            return assignment.result.getOrNull()
        } catch (ex: ArgumentNotFoundException) {
            warning("Missing argument '${ex.missingType.simpleName}' for $resolver")
        } catch (ex: Exception) {
            severe("Failed to resolve element by $resolver!")
            ex.printStackTrace()
        }
        return null
    }

    /**
     * 解析分析后的数据
     */
    suspend fun resolveAnalyzed(analyzed: AnalyzedData, context: ArgumentContext): Unit = coroutineScope {
        for ((node, parts) in analyzed.mappings) {
            launch {
                val resolveTasks = parts.map { part ->
                    val task = part.assignment
                    // 标签片段
                    async {
                        task?.let {
                            // 解析标签
                            resolveTag(it, context)
                        } ?: part.text // 非标签片段或者解析失败, 返回原始内容
                    }
                }
                // 等待所有解析完成
                val result = resolveTasks.awaitAll().joinToString("")
                // 替换原节点的内容
                node.value = JsonPrimitive(result)
            }
        }
    }

    /**
     * 分析后的数据
     */
    class AnalyzedData internal constructor(
        internal val mappings: Map<JsonTree.PrimitiveNode, List<UnresolvedPart>> = HashMap(),
    )

    /**
     * 未解析的部分
     */
    internal class UnresolvedPart(
        /** 原始文本 **/
        val text: String,
        /** 解析任务 (空代表这部分不是有标签的部分) **/
        val assignment: ItemTagResolver.Assignment?,
    )

    /** 标签读取器 **/
    private val reader = VariableReader("{", "}")

    /** 标签参数分隔符 **/
    private const val TAG_ARG_SEPARATION = ":"

}