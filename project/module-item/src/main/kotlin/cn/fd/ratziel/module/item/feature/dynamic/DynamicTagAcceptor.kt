package cn.fd.ratziel.module.item.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.item.api.ComponentHolder
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.feature.virtual.VirtualItemRenderer
import cn.fd.ratziel.module.item.impl.component.ItemDisplay
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TextReplacementConfig

/**
 * DynamicTagAcceptor
 *
 * @author TheFloodDragon
 * @since 2025/8/3 13:29
 */
object DynamicTagAcceptor : VirtualItemRenderer.Acceptor {

    /**
     * 只有动态物品才会应用
     */
    override fun wouldChange(context: ArgumentContext): Boolean {
        return DynamicTagResolver.isDynamic[context]
    }

    override fun accept(actual: NeoItem, context: ArgumentContext) {
        if (actual !is ComponentHolder) return

        // 读取显示组件
        val display = actual.getComponent(ItemDisplay::class.java)

        // 创建文本替换配置
        val replacementConfig = createReplacementConfig(context)

        runBlocking {
            // 显示名称处理
            val newName = display.name?.run { async { replaceText(replacementConfig) } }
            // 本地化名称处理
            val newLocalName = display.localizedName?.run { async { replaceText(replacementConfig) } }
            // Lore 处理
            val newLore = display.lore?.map {
                // 处理并分割换行符
                async { it.replaceText(replacementConfig).let { split(it, "\\n") } }
            }

            // 创建新显示组件
            val newDisplay = ItemDisplay(
                newName?.await(),
                newLocalName?.await(),
                newLore?.awaitAll()?.flatten()
            )

            // 将新的组件写入物品
            actual.setComponent(newDisplay)
        }
    }

    /**
     * 创建文本替换配置
     *
     * @param context 上下文
     * @return 文本替换配置
     */
    fun createReplacementConfig(context: ArgumentContext) = TextReplacementConfig.builder().apply {
        match(DynamicTagResolver.regex)
        replacement { text ->
            val resolved = DynamicTagResolver.resolveTag(text.content(), context)
                ?: return@replacement text // 如果解析失败, 返回原文本
            Message.buildMessage(resolved)
        }
    }.build()

    /**
     * 将一个 Component 按照 delimiter 拆分，返回拆分后的多个 Component，
     * 每一段保留原 Component 在该段文字上的样式、点击/悬停等信息。
     *
     * 注意：这个实现 *假设* 被拆分的 component 是一个 “平铺” 的 TextComponent 或其 children
     * 的组合，不考虑如 TranslatableComponent、NBTComponent 等复杂类型。
     */
    fun split(
        component: Component,
        delimiter: String,
    ): List<Component> {
        val results = mutableListOf<Component>()

        // 递归处理：将 component 树 “按文字” 拆分为段落，每段保留对应 style/children
        fun handle(comp: Component, currentBuilder: ComponentBuilder<*, *>, tailText: String) {
            var builder = currentBuilder
            when (comp) {
                is TextComponent -> {
                    val text = comp.content()
                    // 如果文本中不包含 delimiter，直接 append 整段
                    if (!text.contains(delimiter)) {
                        builder.append(comp.style(comp.style()))  // 保留 style
                            .append(Component.text(text))
                        return
                    }
                    // 含有 delimiter，要拆开
                    val parts = text.split(delimiter)
                    for ((index, part) in parts.withIndex()) {
                        // 对应的 style 与点击/悬停/插入等元数据全部从原 comp 复制
                        val styled = Component.text(part)
                            .style(comp.style())
                        builder.append(styled)

                        // 如果不是最后一段，则该位置为分隔处，切断当前Builder，push 出一个结果，
                        // 并从新的 builder 继续
                        if (index < parts.size - 1) {
                            results.add(builder.build())
                            // 新建 builder，且继承上一段 builder 的 style? 根据需求决定
                            builder = Component.text().style(builder.build().style())
                        }
                    }
                }

                else -> {
                    // 不是 TextComponent，比如 children 存在结构时
                    // 保留原 style 并遍历子组件
                    val style = comp.style()
                    val builder = Component.text().style(style)
                    for (child in comp.children()) {
                        handle(child, builder, tailText)
                    }
                    currentBuilder.append(builder.build())
                }
            }
        }

        // 初始 builder：一般用空文本并继承根 component 的 style
        val rootBuilder: ComponentBuilder<*, *> =
            Component.text().style(component.style())

        handle(component, rootBuilder, "")

        // 把最后一个 builder build 后加入结果
        results.add(rootBuilder.build())

        return results
    }

}