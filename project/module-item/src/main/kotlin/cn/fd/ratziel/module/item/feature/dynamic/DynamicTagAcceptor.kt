package cn.fd.ratziel.module.item.feature.dynamic

import cn.fd.ratziel.common.message.Message
import cn.fd.ratziel.common.message.splitBy
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.feature.virtual.VirtualItemRenderer
import cn.fd.ratziel.module.item.impl.component.dsl
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
        if (actual !is ItemComponentHolder) return

        val dsl = actual.dsl()
        val displayName = dsl.displayName
        val itemName = dsl.itemName
        val lore = dsl.lore
        if (displayName == null && itemName == null && lore.isEmpty()) return

        // 创建文本替换配置
        val replacementConfig = createReplacementConfig(context)

        runBlocking {
            dsl.displayName = displayName?.run { async { replaceText(replacementConfig) } }?.await()
            dsl.itemName = itemName?.run { async { replaceText(replacementConfig) } }?.await()
            if (lore.isNotEmpty()) {
                dsl.lore = lore.map { async { it.replaceText(replacementConfig) } }.awaitAll()
                    .flatMap { it.splitBy("\\n") }
            }
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