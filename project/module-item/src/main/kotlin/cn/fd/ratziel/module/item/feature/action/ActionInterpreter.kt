package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.common.block.BlockBuilder
import cn.fd.ratziel.common.block.BlockScope
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.block.scope.ItemScope
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
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

    override suspend fun preFlow(stream: ItemStream) {
        // 解析动作表
        val actionMap = parseElement(stream.identifier, stream.fetchElement()) ?: return
        // 加入到服务里
        ActionManager.service[stream.identifier] = actionMap
    }

    /**
     * 从配置中解析成动作表
     */
    suspend fun parseElement(identifier: Identifier, element: Element): ActionMap? {
        val property = element.property
        // 仅处理 JsonObject 类型
        if (property !is JsonObject) return null
        // 获取原始动作
        val raw = property.getBy(*nodeNames) ?: return null
        if (raw is JsonObject) {
            // 解析动作表
            val blocks = supervisorScope {
                raw.mapNotNull { (name, code) ->
                    // 匹配触发器
                    val trigger = ActionManager.matchTrigger(name) ?: return@mapNotNull null
                    async {
                        // 构建脚本块
                        val block = if (trigger is ItemTrigger) {
                            // 使用自定义构建函数
                            trigger.build(identifier, element.copyOf(code))
                        } else {
                            // 使用默认构建器
                            BlockBuilder.build(element.copyOf(code), BlockScope.ItemScope)
                        }
                        // 返回结果
                        trigger to block
                    }
                }.awaitAll()
            }
            return ActionMap(blocks.toMap())
        } else throw IllegalArgumentException("Incorrect action format! Unexpected: $raw")
    }

}