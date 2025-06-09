package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.api.action.ActionMap
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
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
@ItemInterpreter.PreInterpretable
object ActionInterpreter : ItemInterpreter {

    /**
     * 动作节点名称
     */
    val nodeNames = arrayOf("action", "actions", "event", "events")

    /**
     * 从配置中解析成触发器表
     */
    suspend fun parse(identifier: Identifier, element: JsonObject): ActionMap = coroutineScope {
        val tasks = element.mapNotNull { (name, code) ->
            // 匹配触发器
            val trigger = ActionManager.matchTrigger(name) ?: return@mapNotNull null
            async {
                // 构建脚本块
                val block = trigger.build(identifier, code)
                // 创建动作
                val action = SimpleAction(code, block)
                // 返回结果
                trigger to action
            }
        }
        ActionMap(tasks.awaitAll().toMap())
    }


    override suspend fun interpret(stream: ItemStream) {
        val property = stream.fetchElement()
        // 仅处理 JsonObject 类型
        if (property !is JsonObject) return
        // 获取原始动作
        val raw = property.getBy(*nodeNames) ?: return
        if (raw is JsonObject) {
            // 解析触发器表
            val triggerMap = parse(stream.identifier, raw)
            // 加入到动作表中
            ActionManager.service[stream.identifier] = triggerMap
        } else throw IllegalArgumentException("Incorrect action format! Unexpected: $raw")
    }

}