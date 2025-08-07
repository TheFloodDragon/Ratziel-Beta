package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.functional.AttachedContext
import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.feature.action.ActionInterpreter
import cn.fd.ratziel.module.item.feature.action.ActionMap
import cn.fd.ratziel.module.item.feature.template.TemplateParser.INHERIT_ALIAS
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * InheritInterpreter - 继承解释器
 *
 * @author TheFloodDragon
 * @since 2025/8/6 22:34
 */
object InheritInterpreter : ItemInterpreter {

    val actionsChain = AttachedContext.Catcher(this) { emptyList<Pair<Template, ActionMap?>>() }

    override suspend fun preFlow(stream: ItemStream) {
        stream.tree.withValue { tree ->
            // 处理树
            val chain = resolveTree(tree)
            // 处理标签
            resolveTag(tree)
            // 处理动作链
            coroutineScope {
                val actionMaps = chain.map {
                    async {
                        // 解析动作表
                        it to ActionInterpreter.parseRoot(stream.identifier, it.element.property)
                    }
                }.awaitAll()
                // 设置动作链
                actionsChain[stream.context] = actionMaps
                // 将 InheritResponder 绑定到其触发器上
                for (actionMap in actionMaps.mapNotNull { it.second }) {
                    // 优先级必须高于 ItemResponder
                    actionMap.map.keys.forEach { it.bind(InheritResponder, -1) }
                }
            }
        }
    }

    /**
     * 解析树并合并模板
     */
    @JvmStatic
    fun resolveTree(tree: JsonTree): List<Template> {
        val node = tree.root
        // 处理对象节点
        if (node !is JsonTree.ObjectNode) return emptyList()
        // 寻找继承字段
        val field = node.value.getBy(*INHERIT_ALIAS) as? JsonTree.PrimitiveNode ?: return emptyList()
        // 获取模板
        val template = TemplateParser.findTemplate(field.value.content) ?: return emptyList()
        // 合并对象 (从底部开始)
        val availableChain = ArrayList<Template>()
        for (t in template.asChain()) {
            val element = TemplateParser.findElement(t) ?: break // 出错了就直接退出吧, 不应用上面的了
            availableChain.add(t)
            // 过滤动作信息 (不合并这些)
            val filtered = element.filter { ActionInterpreter.nodeNames.contains(it.key) }
            // 不替换原有的合并
            TemplateParser.merge(node, filtered)
        }
        return availableChain
    }

    /**
     * 解析标签
     */
    @JvmStatic
    fun resolveTag(tree: JsonTree) {
        // 直接调用标签解析器 (已知其不需要上下文信息)
        TaggedSectionResolver.resolveSingle(InheritResolver, tree, SimpleContext())
    }

}