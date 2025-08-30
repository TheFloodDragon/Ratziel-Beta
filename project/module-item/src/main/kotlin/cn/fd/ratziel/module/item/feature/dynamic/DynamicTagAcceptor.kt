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
            val newLore = display.lore?.map { it.run { async { replaceText(replacementConfig) } } }

            // 创建新显示组件
            val newDisplay = ItemDisplay(
                newName?.await(),
                newLocalName?.await(),
                newLore?.awaitAll()
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

}