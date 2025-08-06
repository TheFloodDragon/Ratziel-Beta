package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.script.block.BlockBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject

/**
 * ActionInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/5/10 16:38
 */
object ActionInterpreter : ItemInterpreter {

    /**
     * 动作节点名称
     */
    val nodeNames = arrayOf("action", "actions", "event", "events")

    /**
     * 从配置中解析成动作表
     */
    suspend fun parse(identifier: Identifier, element: JsonObject): ActionMap = coroutineScope {
        element.mapNotNull { (name, code) ->
            // 匹配触发器
            val trigger = ActionManager.matchTrigger(name) ?: return@mapNotNull null
            async {
                // 构建脚本块
                val block = if (trigger is ItemTrigger) {
                    // 使用自定义构建函数
                    trigger.build(identifier, element)
                } else {
                    // 使用默认构建器
                    BlockBuilder.build(code)
                }
                // 返回结果
                trigger to block
            }
        }.awaitAll().toMap().let { ActionMap(it) }
    }

    override suspend fun preFlow(stream: ItemStream) {
        val property = stream.fetchElement()
        // 仅处理 JsonObject 类型
        if (property !is JsonObject) return
        // 获取原始动作
        val raw = property.getBy(*nodeNames) ?: return
        if (raw is JsonObject) {
            // 解析动作表
            val actionMap = parse(stream.identifier, raw)
            // 加入到服务中
            ActionManager.service[stream.identifier] = actionMap
        } else throw IllegalArgumentException("Incorrect action format! Unexpected: $raw")
    }

}