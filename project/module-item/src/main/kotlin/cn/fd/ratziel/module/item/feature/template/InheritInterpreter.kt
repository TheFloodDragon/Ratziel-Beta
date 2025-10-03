package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.feature.action.ActionInterpreter
import cn.fd.ratziel.module.item.feature.action.ActionMap
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
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

    val templateActions = AttachedContext.catcher(this) { emptyMap<Template, List<ActionMap>>() }

    override suspend fun preFlow(stream: ItemStream) {
        stream.tree.withValue { tree ->
            // 处理树
            val templates = resolveTree(tree)

            // 处理标签
            val self = stream.origin.copyOf(tree.toElement())
            stream.context.put(self)
            DefaultResolver.resolveBy(InheritResolver, tree, stream.context)
            stream.context.remove(self::class.java)

            // 处理动作链
            coroutineScope {
                val actionsGroup: Map<Template, List<ActionMap>> = templates.associateWith { template ->
                    // 动作是要从父开始执行的, 所以要反转动作链, 变成自上而下的
                    template.dependencyChain.asReversed().map {
                        async {
                            // 解析动作表
                            ActionInterpreter.parseElement(stream.identifier, it.origin)
                        }
                    }.awaitAll().filterNotNull()
                }
                if (actionsGroup.isNotEmpty()) {
                    // 设置模板的动作链
                    templateActions[stream.context] = actionsGroup
                    // 将 InheritResponder 绑定到其触发器上
                    for (actionMap in actionsGroup.values.flatMap { it }) {
                        // 优先级必须高于 ItemResponder
                        actionMap.map.keys.forEach { it.bind(InheritResponder, -1) }
                    }
                }
            }
        }
    }

    /**
     * 解析树并合并模板
     */
    @JvmStatic
    fun resolveTree(tree: JsonTree): List<Template> {
        val root = tree.root
        // 处理对象节点
        if (root !is JsonTree.ObjectNode) return emptyList()
        // 寻找继承字段
        val templates = TemplateParser.findParents(root).mapNotNull {
            TemplateElement.findBy(it)
        }
        for (template in templates) {
            val element = template.element
            // 过滤动作信息 (不合并这些)
            val filtered = element.filter { !ActionInterpreter.nodeNames.contains(it.key) }
            // 不替换原有的合并
            TemplateParser.merge(root, filtered)
        }
        return templates
    }

}